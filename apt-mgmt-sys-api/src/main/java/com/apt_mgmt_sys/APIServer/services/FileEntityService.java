package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.PageRequest;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.FileEntity;
import com.apt_mgmt_sys.APIServer.repositories.FileEntityRepository;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileEntityService {
    @Autowired
    private FileEntityRepository repository;

    @Value("${uploads_folder_path}")
    private String uploadsFolderPath;

    @Value("${temp_folder_path}")
    private String tempFolderPath;

    public List<FileEntity> getAll(Integer offset, Integer limit, String field, Sort.Direction sortDir){
        Sort sort = null;
        if (field != null){
            sort = new Sort(field);
        }
        if (sortDir != null){
            sort = sort == null ? new Sort(sortDir) : sort.and(new Sort(sortDir));
        }
        PageRequest pageReq = new PageRequest(offset, limit, sort);
        Page<FileEntity> page = repository.findAll(pageReq);
        if (page == null) throw new IllegalArgumentException(String.format("Unable to retrieve page for query set {offset=%s, limit=%s, field=%s sortDir=%s",
                offset,limit,field,sortDir));
        return page.getContent();
    }

    public List<FileEntity> getAll(){
        return getAll(null, null, null, null);
    }

    public FileEntity getById(Long id){
        return repository.findOne(id);
    }

    public List<FileEntity> listByTypeAndId(TransactionType type, Long id, PageRequest pageRequest){
        List<FileEntity> fileEntities = repository.findByEntityTypeAndEntityId(type, id, pageRequest);
        return fileEntities;
    }

    public FileEntity create(Map<String, Object> json, InputStream fileStream) throws Exception{
        FileEntity fileEntity = new FileEntity();

        Utilities.updateBlock("name", json, String.class, x -> fileEntity.setName(x)).required();
        Utilities.updateBlock("entityType", json, String.class, x -> fileEntity.setEntityType(TransactionType.valueOf(x))).required();

        Utilities.updateBlock("entityId", json, Number.class, x -> fileEntity.setEntityId(x.longValue()));

        FileEntity savedFileEntity = repository.save(fileEntity);
        String filehash = generateFileHash(savedFileEntity.getEntityId(), savedFileEntity.getName(), savedFileEntity.getEntityType());
        System.out.println("filename: " + savedFileEntity.getName() + " filehash: " + filehash);
        String location = uploadsPath(savedFileEntity.getTemp()) + File.separator + filehash;
        System.out.println(location);
        savedFileEntity.setLocation(location);
        writeFile(savedFileEntity, fileStream, savedFileEntity.getTemp());
        return repository.save(savedFileEntity);
    }

    public FileEntity updateById(Long id, Map<String, Object> json, InputStream fileStream){
        FileEntity fileEntity = getById(id);
        if (fileEntity == null) throw new IllegalArgumentException("Cannot find FileEntity id : " + id);
        Utilities.updateBlock("name", json, String.class, x -> fileEntity.setName(x));
        return repository.save(fileEntity);
    }
    public List<FileEntity> bulkUpdateAndRemoveFileEntity(Map<String, Object> json, Long entityId, TransactionType transactionType){
        List<Integer> requestedFileEntities = (List)Utilities.getFieldFromJSON(json, "fileEntities", List.class);
        if (requestedFileEntities == null) return new ArrayList<>();
        Set<Integer> newFileEntityIds = new HashSet<>(requestedFileEntities);
        List<FileEntity> previousFileEntities = listByTypeAndId(transactionType, entityId, null);
        previousFileEntities.stream().forEach(fileEntity -> {
            if(newFileEntityIds.contains(fileEntity.getId().intValue())){
                newFileEntityIds.remove(fileEntity.getId().intValue());
            } else {
                removeAndMoveFileToTemp(fileEntity);
}
        });
        return newFileEntityIds.stream()
                .map(fileEntityId -> {
                    FileEntity fileEntity = getById(((Number)fileEntityId).longValue());
                    if (fileEntity!=null && fileEntity.getTemp()){
                        return updateEntityIdAndMoveFileToUploads(fileEntity, entityId);
                    }
                    return null;
                })
                .collect(Collectors.toList());

    }

    public FileEntity updateEntityIdAndMoveFileToUploads(FileEntity fileEntity, Long entityId){
        if(fileEntity.getTemp() && fileEntity.getLocation().contains(tempFolderPath)){
            String oldFileLocation = fileEntity.getLocation();
            fileEntity.setEntityId(entityId);
            if(fileEntity.getId() == null) throw new IllegalArgumentException("FileEntity does not have ID. Should be registered in the database");
            String filehash  = generateFileHash(fileEntity.getEntityId(), fileEntity.getName(), fileEntity.getEntityType());
            String dirPath = uploadsPath(false);
            fileEntity.setLocation(dirPath + File.separator + filehash);
            createFolderIfNotExisted(dirPath);
            File file = new File(oldFileLocation);
            file.renameTo(new File(fileEntity.getLocation()));
            System.out.printf("Moving FileEntity:\n%s\n-> old location : %s\n-> new location : %s\n",
                    fileEntity.toString(), oldFileLocation, fileEntity.getLocation());
            return repository.save(fileEntity);
        }
        return fileEntity;
    }

    public void removeAndMoveFileToTemp(FileEntity fileEntity){
        if (fileEntity == null) throw new IllegalArgumentException("fileEntity is null");
        if (!fileEntity.getTemp() && fileEntity.getLocation().contains(uploadsFolderPath)){
            String location = fileEntity.getLocation();
            String tempPath = uploadsPath(true);
            String newLocation = tempPath + File.separator
                    + "_deleted_" + generateFileHash(fileEntity.getEntityId(), fileEntity.getName(), fileEntity.getEntityType());
            createFolderIfNotExisted(tempPath);
            File file = new File(location);
            file.renameTo(new File(newLocation));
            System.out.printf("deleted %s and moved to %s\n", fileEntity.getId(), newLocation);
            repository.delete(fileEntity);
        }
    }

    public void removeById(Long id){
        repository.delete(id);
    }

    public String generateFileHash(Long id, String name, TransactionType type){
        String[] nameArr = name.split("\\.");
        System.out.println(Arrays.toString(nameArr));
        if (id == null) id = -1L;
        String ext = "";
        if (nameArr.length > 0){
            ext = "."+nameArr[nameArr.length-1];
        }
        String hexHash = Integer.toHexString(name.hashCode()) + Long.toHexString(new Date().getTime()) + ext;
        return String.format("%s_%s_%s", type.toString(), id, hexHash);
    }

    private void createFolderIfNotExisted(String path){
        File dir = new File(path);
        if (!dir.exists()){
            dir.mkdir();
        }
    }

    public void writeFile(FileEntity fileEntity, InputStream fileStream, Boolean temp) throws IOException {
        String path = uploadsPath(temp);
        createFolderIfNotExisted(path);
        File outputFile = new File(fileEntity.getLocation());
        byte[] data = new byte[fileStream.available()];
        FileOutputStream outStream = new FileOutputStream(outputFile);
        fileStream.read(data);
        outStream.write(data);
        outStream.close();
    }

    public Resource getFileAsResource(FileEntity fileEntity){
        try {
            File file = new File(fileEntity.getLocation());
            return new UrlResource(file.toURI());
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static void printFileStream(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream);
        System.out.println(reader.getEncoding());
        byte[] buff; int buff_n = 0;
        StringBuffer sb = new StringBuffer();
        while((buff_n = stream.available()) > 0){
            buff = new byte[buff_n];
            stream.read(buff);
            sb.append(new String(buff, "UTF-8"));
        }
        System.out.println(sb.toString());
    }

    public String uploadsPath(boolean temp){
        String appRootPath = System.getProperty("user.dir");
        String sysSlash = File.separator;
        String filePath = String.join(sysSlash, Arrays.asList(new String[]{appRootPath, temp ? tempFolderPath : uploadsFolderPath}));
        return filePath;
    }

    public static void makeTestData(FileEntityService service){
        try {
            Map<String, Object> json = Utilities.buildMap(new Pair[]{
                    new Pair("name", "download.html"),
                    new Pair("entityType", TransactionType.CONTRACT.toString()),
            });

            FileEntity fe1 = service.create(json, new FileInputStream(new File("/Users/nsimsiri/Downloads/download.html")));
            service.updateEntityIdAndMoveFileToUploads(fe1, 1L);
        } catch (Exception e){
            e.printStackTrace();
        }

    }


}

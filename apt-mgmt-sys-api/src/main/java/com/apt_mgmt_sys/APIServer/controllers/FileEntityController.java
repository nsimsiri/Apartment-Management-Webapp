package com.apt_mgmt_sys.APIServer.controllers;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.FileEntity;
import com.apt_mgmt_sys.APIServer.services.FileEntityService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/files")
public class FileEntityController {
    @Autowired private FileEntityService service;
    static final String PLURAL = "fileEntities";
    static final String SINGULAR = "fileEntity";
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Map> uploadTemp(@RequestParam(value = "file") MultipartFile file,
                                    String name,
                                    String entityType){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            InputStream stream = file.getInputStream();
            Map<String, Object> json = Utilities.buildMap(new Pair[]{
               new Pair("name", name),
               new Pair("entityType", entityType)
            });
            FileEntity fileEntity = service.create(json, file.getInputStream());
            responseJson.put(SINGULAR, fileEntity);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public ResponseEntity<Map> listByTypeAndId(@RequestParam(value="type") String type,
                                             @RequestParam(value="id") Long id){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            TransactionType transactionType = TransactionType.valueOf(type);
            List<FileEntity> fileEntities = service.listByTypeAndId(transactionType, id, null);
            responseJson.put(PLURAL, fileEntities);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity downloadById(@PathVariable(value = "id") Long id){
        FileEntity fileEntity = service.getById(id);
        String[] names = fileEntity.getLocation().split(File.separator);
        Resource file = service.getFileAsResource(fileEntity);
        System.out.println(file);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }

    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity<Map> getAll(){
        Map<String, Object> responseJson = new HashMap<>();
        try {
            List<FileEntity> all = service.getAll();
            responseJson.put(PLURAL, all);
        } catch(Exception e){
            e.printStackTrace();
            responseJson = new HashMap<>();
            responseJson.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson);
        }
        return ResponseEntity.ok(responseJson);
    }
}

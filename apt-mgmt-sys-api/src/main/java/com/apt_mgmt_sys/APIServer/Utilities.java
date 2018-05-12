package com.apt_mgmt_sys.APIServer;

import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.security.Role;
import javafx.util.Pair;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;

public class Utilities {

    public static class Options<T> {
        private final Object object;
        private final Class<T> clazz;
        private final String key;
        private final Map<String, Object> schema;
        public Options(final Object object, final Class<T> clazz, final String key, final Map<String, Object> schema){
            this.object = object;
            this.clazz = clazz;
            this.key = key;
            this.schema = schema;
        }

        public Options<T> required(){
            if (this.object == null) throw new IllegalArgumentException(String.format("[FIELD REQUIREMENT ERR]: null field \"%s\" for json: %s", key, schema));
            if (!clazz.isAssignableFrom(object.getClass())) throw new IllegalArgumentException(
                    String.format("[FIELD REQUIREMENT ERR]: type mismatch for required type %s and data objects type: %s", clazz.getName(), object.getClass().getName())
            );
            return this;
        }

        public T get(){
            return (T) this.object;
        }
    }

    public static <T> Options<T> updateBlock(String key, Map<String, Object> updateSchema, Class<T> c, Consumer<T> f){
        if (updateSchema.containsKey(key)){
            T o = (T)Utilities.getFieldFromJSON(updateSchema, key, c);
            if (o!=null){
                f.accept(o);
            }
            return new Options<T>(o, c, key, updateSchema);
        }
        return new Options<T>(null, c, key, updateSchema);
    }
    public static <T> T getFieldFromJSON(Map<String, Object> json, String key, Class<T> clazz){
        if (json.containsKey(key)){
            T value = (T)json.get(key);
//            System.out.format("key:%s value:%s class:%s\n", key, value, value.getClass().getSimpleName());
            if (value != null && clazz.isAssignableFrom(value.getClass())){
                return value;
            }
        }
        return null;
    }

    public static Date MIN_DATE(){
        return java.sql.Date.valueOf((LocalDate.now().minusYears(1000)));
    }

    public static Date MAX_DATE(){
        return java.sql.Date.valueOf((LocalDate.now().plusYears(1000)));
    }

    public static Date localDateToDate(LocalDate localDate){
        return java.sql.Date.valueOf(localDate);
    }

    public static Map<String, Object> buildAuthenticationResponse(boolean isAuthenticated, String username,
                                                                  User user, List<Role> roles,
                                                                  Integer status, String error) {
        Map<String, Object> m = new HashMap<>();
        roles = roles == null ? new ArrayList<Role>() : roles;
        m.put("isAuthenticated", isAuthenticated);
        m.put("username", username);
        m.put("roles", roles);
        if (user!=null) m.put("user", user);
        m.put("status", status);
        if (error!=null) m.put("error", error);
        return m;

    }
    // d1 < d2 -> -1
    // d1 == d2 -> 0
    // d1 > d2 -> 1
    public static int compareDate(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        Integer c1_date = c1.get(Calendar.DATE);
        Integer c1_month = c1.get(Calendar.MONTH);
        Integer c1_year = c1.get(Calendar.YEAR);
//        System.out.format("[%s/%s/%s] vs " ,c1_date,c1_month,c1_year);

        Integer c2_date = c2.get(Calendar.DATE);
        Integer c2_month = c2.get(Calendar.MONTH);
        Integer c2_year = c2.get(Calendar.YEAR);
//        System.out.format("[%s/%s/%s] -> (%s/%s/%s)\n",c2_date,c2_month,c2_year, c1_date.compareTo(c2_date), c1_month.compareTo(c2_month),c1_year.compareTo(c2_year));

        if (c1_year.compareTo(c2_year) == 0) {
            if (c1_month.compareTo(c2_month) == 0) {
                return c1_date.compareTo(c2_date);
            } else return c1_month.compareTo(c2_month);

        } else return c1_year.compareTo(c2_year);

    }

    public static boolean validateDateAfterToday(Date date){
        Date today = Calendar.getInstance().getTime();
        return compareDate(today, date) != 1;
    }

    public static <T> List<T> iterableToList(Iterable<T> itr){
        List<T> Out = new ArrayList<>();
        itr.forEach(x -> Out.add(x));
        return Out;
    }

    public static <T, K> List<K> iterableToList(Iterable<T> itr, Class<K> typeSuper){
        List<K> Out = new ArrayList<>();
        itr.forEach(x -> {
            if (!(typeSuper.isAssignableFrom(x.getClass())))
                throw new IllegalArgumentException("[Utilities.iterableToList] cannot cast iterable of type" +
                        x.getClass().getSimpleName() + " to type " + typeSuper.getSimpleName());
            Out.add(typeSuper.cast(x));
        });
        return Out;
    }

    public static <T, K> Map<T, K> buildMap(Pair<T,K>[] pairs){
        Map<T, K> m = new HashMap<>();
        for(Pair<T, K> edge : pairs){
            m.put(edge.getKey(), edge.getValue());
        }
        return m;
    }

    public static final String ANSI_RESET = "\u001B[0m";

    public static String SUCCESS_STR(String str){
        return "\u001B[32m"+str+ANSI_RESET;
    }

    public static String FAIL_STR(String str){
        return "\u001B[31m"+str+ANSI_RESET;
    }

    public static void main(String[] args) throws Exception{
//        String appRootPath = System.getProperty("user.dir");
//        String sysSlash = File.separator;
//        String filePath = String.join(sysSlash, Arrays.asList(new String[]{appRootPath, "src", "main", "resources","uploads"}));
//        System.out.println(filePath);
//        File dir = new File(filePath);
//        System.out.println(dir.exists());
//        if (!dir.exists()){
//            dir.mkdir();
//        }
//        System.out.println(dir.exists());

//        boolean b = Number.class.isAssignableFrom(Integer.class);
//        System.out.println(b);
        Calendar c1 = Calendar.getInstance();
//        System.out.printf("%s %s %s\n", c1.get(Calendar.DATE), c1.get(Calendar.MONTH), c1.get(Calendar.YEAR));
//        Calendar c2 = Calendar.getInstance();
//        SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy");
//        Date d = sdf_1.parse("01/08/2018");
//        Date d2 = sdf_1.parse("02/08/2018");
//        Date d3 = sdf_1.parse("27/11/2017");
//        Date d4 = sdf_1.parse("28/11/2017");
//        Date d5 = sdf_1.parse("29/11/2017");
//        System.out.println(d);
//        System.out.println(d2);
//        System.out.println(compareDate(d,d2));
//        System.out.println(validateDateAfterToday(d3));
//        System.out.println(validateDateAfterToday(d4));
//        System.out.println(validateDateAfterToday(d5));
        Set<Integer> s = new HashSet<Integer>(new ArrayList<Integer>(Arrays.asList(new Integer[]{2,3})));
        Number y = null;
        Integer x = (Integer) y;
//        System.out.println(MIN_DATE());
//        System.out.println(MAX_DATE());
//        System.out.println(compareDate(MIN_DATE(), new Date()));
//        System.out.println(compareDate(MAX_DATE(), new Date()));
//        System.out.println(java.sql.Date.valueOf(LocalDate.now()));

    }
}

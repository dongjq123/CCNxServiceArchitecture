package edu.bupt.sia.ccn.test;

import edu.bupt.sia.ccn.ServiceNameParser;

/**
 * Created by fish on 16-4-28.
 */
public class PWTest {
    public static void main(String[] args){
        ServiceNameParser serviceNameParser = new ServiceNameParser();
        byte[] bytes = serviceNameParser.encrypt("{\"servicename\":\"edu.bupt.service.htmlparse\",\"args\":[\"ccnx:/contents/test.html\",\"ccnx:/contents/out/test.html\"]}");
        System.out.println(new String(bytes));
    }
}

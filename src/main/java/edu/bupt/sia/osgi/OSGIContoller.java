package edu.bupt.sia.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fish on 16-4-11.
 */
public class OSGIContoller {
    private Framework framework;
    private BundleContext bundleContext;
    final static protected String CCNServiceTag = "CCNService";

    public OSGIContoller(){
        this.framework = OSGILauncher.getFramework();
        this.bundleContext = framework.getBundleContext();
        //add initial bundles
        try {
//            Bundle ccnbundle01 = bundleContext.installBundle("file:libs/org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar");
//            ccnbundle01.start();
//            Bundle ccnbundle02 = bundleContext.installBundle("file:libs/org.apache.felix.gogo.shell_0.10.0.v201212101605.jar");
//            ccnbundle02.start();
//            Bundle ccnbundle03 = bundleContext.installBundle("file:libs/org.apache.felix.gogo.command_0.10.0.v201209301215.jar");
//            ccnbundle03.start();

            Bundle ccnbundle = bundleContext.installBundle("file:libs/ccn-bundle_1.0.0.jar");
            ccnbundle.start();
            Bundle ccniobundle = bundleContext.installBundle("file:libs/CCNIOService.jar");
            ccniobundle.start();
            Bundle ccnservicebundle = bundleContext.installBundle("file:libs/CCNIOServiceInstall.jar");
            ccnservicebundle.start();
        } catch (BundleException e) {
            e.printStackTrace();
        }
    }

    public Bundle installBundle(String path){
        Bundle bundle = null;
        try {
            bundle = bundleContext.installBundle(path);
            bundle.start();
        } catch (BundleException e) {
            System.out.println("installBundle exception:"+path);
            e.printStackTrace();
        }
        return bundle;
    }

    public Bundle installBundleByCCNIOStream(String bundleName){
        Bundle bundle = null;
        try {
            ServiceReference sr = bundleContext.getServiceReference("com.fish.service.controller.CCNServiceInstall");
            Object ccnServiceInstall = bundleContext.getService(sr);
            if(ccnServiceInstall != null) {
                long id = 0;
                Method[] methods = ccnServiceInstall.getClass().getDeclaredMethods();
                for (Method m: methods) {
                    if (m.getName().equals("installService")){
                        id = (long)m.invoke(ccnServiceInstall, bundleName);
                    }
                }
                bundle = bundleContext.getBundle(id);
                if (bundle != null){
                    bundle.start();
                }
            }else {
                System.err.println("get CCNServiceInstall error!");
            }
        } catch (BundleException e) {
            System.out.println("installBundle by InputStream exception:"+bundleName);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    public void executeServiceByID(long bundleID, String[] args){
        Bundle b = bundleContext.getBundle(bundleID);
        String ServiceEntryName = b.getHeaders().get(CCNServiceTag);
//        String ServiceEntryName = "";
        if(ServiceEntryName != null && ServiceEntryName.length() > 0) {
            ServiceReference sr = bundleContext.getServiceReference(ServiceEntryName);
            Object o = bundleContext.getService(sr);
            try {
                Method m = o.getClass().getMethod("execute", String[].class);
                m.invoke(o, new Object[]{args});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                System.out.println("Can't find execute method!!");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else{
            System.err.println("executeServiceByID : Can't get Service entry class");
        }

    }

    public PipedInputStream executeServiceBySymbolicName(String bundleSymbolicName, String[] args){
        Bundle b = bundleContext.getBundle(bundleSymbolicName);
        String ServiceEntryName = b.getHeaders().get(CCNServiceTag);
//        String ServiceEntryName = "";
        if(ServiceEntryName != null && ServiceEntryName.length() > 0) {
            ServiceReference sr = bundleContext.getServiceReference(ServiceEntryName);
            Object o = bundleContext.getService(sr);
            try {
                Method m = o.getClass().getMethod("execute", String[].class);
                Object re = m.invoke(o, new Object[]{args});
                if(re instanceof PipedInputStream){
                    return (PipedInputStream)re;
                }else{
                    return null;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                System.out.println("Can't find execute method!!");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else{
            System.err.println("executeServiceBySymbolicName : Can't get Service entry class");
        }
        return null;
    }

    public void updateServiceByID(long bundleID){
    }

    public void removeServiceByID(long bundleID){
        Bundle b = bundleContext.getBundle(bundleID);
        try {
            b.stop();//
            b.uninstall();
        } catch (BundleException e) {
            e.printStackTrace();
        }
    }

    public void removeServiceBySymbolicName(String bundleSymbolicName){
        Bundle b = bundleContext.getBundle(bundleSymbolicName);
        try {
            b.stop();
            b.uninstall();
        } catch (BundleException e) {
            e.printStackTrace();
        }
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}

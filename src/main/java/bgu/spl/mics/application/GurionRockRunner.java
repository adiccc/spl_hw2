package bgu.spl.mics.application;

import bgu.spl.mics.handllers.FileHandelUtil;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.handllers.Parser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {
    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        if(true){
//        if(args.length>0){
            StatisticalFolder statisticalFolder = new StatisticalFolder();
            List<Camera> cameras = new ArrayList<>();
            List<CameraService> camerasServices = new ArrayList<>();
            List<LiDarWorkerTracker> liDarWorkerTrackers = new ArrayList<>();
            List<LiDarService> liDarServices = new ArrayList<>();
            int tickTime=0;
            int duration=0;
            TimeService timeService;
            GPSIMU gpsimu;
            PoseService poseService=null;
//            String configurationPath=args[0];
            String configurationPath="./lastUpdatedExample/lastExample_2/configuration_file.json";
            String folderPath=configurationPath.substring(0,configurationPath.length()-23);
            //init fusion slam instance
            FusionSlam.getInstance().initInstance(statisticalFolder,folderPath);
            FusionSlamService fusionSlamService=new FusionSlamService(FusionSlam.getInstance());
//            JsonObject rootObject = FileHandelUtil.readJsonObject(configurationPath);
            JsonObject rootObject = FileHandelUtil.readJsonObject("./lastUpdatedExample/lastExample_2/configuration_file.json");
            Set<String> keys = rootObject.keySet();
            for (String key : keys) {
                JsonElement element = rootObject.get(key);

                switch (key) {
                    case "Cameras":
                        cameras= Parser.handleCameras(element.getAsJsonObject(),statisticalFolder,folderPath);
                        if(cameras==null || cameras.size()==0){
                            System.out.println("Cameras configuration failed, closing the program");
                            return;
                        }
                        camerasServices=handelCamerasService(cameras);
                        break;

                    case "LiDarWorkers":
                        liDarWorkerTrackers=Parser.handleLidarWorkers(element.getAsJsonObject(),statisticalFolder,folderPath);
                        if(liDarWorkerTrackers==null || liDarWorkerTrackers.isEmpty()){
                            System.out.println("Lidar worker configuration failed, closing the program");
                            return;
                        }
                        liDarServices=handelLidarService(liDarWorkerTrackers);
                        break;

                    case "poseJsonFile":
                        if(element.getAsString().length()<3){
                            System.out.println("GPS file path is missing - configuration failed, closing the program");
                            return;
                        }
                        gpsimu=new GPSIMU(folderPath+element.getAsString().substring(1));
                        if(gpsimu.getStatus()==STATUS.ERROR){
                            System.out.println("GPS configuration failed, closing the program");
                            return;
                        }
                        poseService=new PoseService(gpsimu);
                        break;

                    case "TickTime":
                        tickTime=Parser.parseInt(element);
                        if(tickTime==-1){
                            System.out.println("Time ticktime configuration is out of range  or wrong type value, closing the program");
                            return;
                        }
                        break;

                    case "Duration":
                        duration=Parser.parseInt(element);
                        if(duration<=0){
                            System.out.println("Time duration configuration is out of range or wrong type value, closing the program");
                            return;
                        }
                        break;

                    default:
                        System.out.println("Recognise unknown key: " + key+", closing the program");
                        return;
                }
            }
            timeService=new TimeService(tickTime,duration,statisticalFolder);

            //Start the simulation.
            List<Thread> allThreads=new LinkedList<>();
            if(poseService!=null)
                allThreads.add(new Thread(poseService));
            for(CameraService c: camerasServices)
                allThreads.add(new Thread(c));
            for(LiDarService l: liDarServices)
                allThreads.add(new Thread(l));
            allThreads.add(new Thread(fusionSlamService));
            allThreads.add(new Thread(timeService));
            FusionSlam.getInstance().setNumberOfSensors(allThreads.size()-2);
            MessageBusImpl.latch=new CountDownLatch(allThreads.size());
            System.out.println("=========================================================================================");
            System.out.println("=============================  Gurion Rock start cleaning  ==============================");
            System.out.println("=========================================================================================");
            for (Thread t : allThreads) {
                t.start();
            }
        }
        return;
    }


    private static List<LiDarService> handelLidarService(List<LiDarWorkerTracker> liDarWorkerTrackers) {
        List<LiDarService> liDarServices = new ArrayList<>();
        int index=1;
        for(LiDarWorkerTracker liDarWorkerTracker : liDarWorkerTrackers) {
            liDarServices.add(new LiDarService(liDarWorkerTracker,index));
            index++;
        }
        return liDarServices;
    }


    public static List<CameraService> handelCamerasService(List<Camera> cameras) {
        List<CameraService> camerasServices = new ArrayList<>();
        int index=1;
        for (Camera camera : cameras) {
            camerasServices.add(new CameraService(camera));
            index++;
        }
        return camerasServices;
    }



}

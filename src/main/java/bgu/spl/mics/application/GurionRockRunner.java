package bgu.spl.mics.application;

import bgu.spl.mics.FileHandelUtil;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
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
        System.out.println("Hello World!");
//        if(args.length>0){
        if(true){
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
            String configurationPath="./example_input/configuration_file.json";
            String outputPath=configurationPath.substring(0,configurationPath.length()-23);
            FusionSlamService fusionSlamService=new FusionSlamService(FusionSlam.getInstance(statisticalFolder, outputPath));
//            JsonObject rootObject = FileReaderUtil.readJson(configurationPath);
            JsonObject rootObject = FileHandelUtil.readJsonObject("./example_input/configuration_file.json");
            Set<String> keys = rootObject.keySet();
            int ThreadCounter=0;
            for (String key : keys) {
                JsonElement element = rootObject.get(key);

                switch (key) {
                    case "Cameras":
                        System.out.println("Cameras");
                        cameras=handleCameras(element.getAsJsonObject(),statisticalFolder);
                        camerasServices=handelCamerasService(cameras);
                        break;

                    case "LidarWorkers":
                        System.out.println("LidarWorkers");
                        liDarWorkerTrackers=handleLidarWorkers(element.getAsJsonObject(),statisticalFolder);
                        liDarServices=handelLidarService(liDarWorkerTrackers);
                        break;

                    case "poseJsonFile":
                        System.out.println("poseJsonFile");
                        gpsimu=new GPSIMU(element.getAsString());
                        poseService=new PoseService(gpsimu);
                        break;

                    case "TickTime":
                        System.out.println("TickTime");
                        tickTime=element.getAsInt();
                        break;

                    case "Duration":
                        System.out.println("Duration");
                        duration=element.getAsInt();
                        break;

                    default:
                        System.out.println("Unknown key: " + key);
                }
            }
            timeService=new TimeService(tickTime,duration,statisticalFolder);
            System.out.println("Gurion Rock Runner start threads");
//            Start the simulation.
            List<Thread> allThreads=new LinkedList<>();
            if(poseService!=null)
                allThreads.add(new Thread(poseService));
            for(CameraService c: camerasServices)
                allThreads.add(new Thread(c));
            for(LiDarService l: liDarServices)
                allThreads.add(new Thread(l));
            allThreads.add(new Thread(fusionSlamService));
            allThreads.add(new Thread(timeService));
            MessageBusImpl.latch=new CountDownLatch(allThreads.size());
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

    private static List<LiDarWorkerTracker> handleLidarWorkers(JsonObject jsonObject,StatisticalFolder statisticalFolder) {
        List<LiDarWorkerTracker> liDarWorkerTrackers = new ArrayList<>();
        int index=1;
        // Extract the CamerasConfigurations array
        JsonArray workersConfig = jsonObject.getAsJsonArray("LidarConfigurations");

        // Get the lidar database file path
        String filePath=jsonObject.get("lidars_data_path").getAsString();

        // Iterate through the cameras configurations
        for (JsonElement lidarElement : workersConfig) {
            JsonObject lidarObj = lidarElement.getAsJsonObject();

            // Extract lidar properties
            int id = lidarObj.get("id").getAsInt();
            int frequency = lidarObj.get("frequency").getAsInt();
            STATUS status = STATUS.DOWN;

            // Create a Camera object and add it to the list
            LiDarWorkerTracker lidar = new LiDarWorkerTracker(id, frequency, status,filePath,statisticalFolder);
            liDarWorkerTrackers.add(lidar);
        }
        return liDarWorkerTrackers;
    }

    private static List<CameraService> handelCamerasService(List<Camera> cameras) {
        List<CameraService> camerasServices = new ArrayList<>();
        int index=1;
        for (Camera camera : cameras) {
            camerasServices.add(new CameraService(camera));
            index++;
        }
        return camerasServices;
    }

    private static List<Camera> handleCameras(JsonObject jsonObject, StatisticalFolder statisticalFolder) {
        List<Camera> cameras = new ArrayList<>();

        // Extract the CamerasConfigurations array
        JsonArray camerasConfig = jsonObject.getAsJsonArray("CamerasConfigurations");

        // Get the camera data file path
        String cameraDataPath = jsonObject.get("camera_datas_path").getAsString();

        // Iterate through the cameras configurations
        for (JsonElement cameraElement : camerasConfig) {
            JsonObject cameraObj = cameraElement.getAsJsonObject();

            // Extract camera properties
            int id = cameraObj.get("id").getAsInt();
            int frequency = cameraObj.get("frequency").getAsInt();
            STATUS status = STATUS.DOWN; // Example logic for status

            // Create a Camera object and add it to the list
            Camera camera = new Camera(id, frequency, status, cameraDataPath,statisticalFolder);
            cameras.add(camera);
        }
        return cameras;
    }

}

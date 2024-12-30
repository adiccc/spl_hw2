package bgu.spl.mics.application.objects;

import bgu.spl.mics.ErrorReport;
import bgu.spl.mics.FileHandelUtil;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private List<TrackedObjectsEvent> trackedObjectsevents;
    private List<PoseEvent> poses;
    private ArrayList<LandMark> landMarks;
    private ConcurrentHashMap<Integer, StampedDetectedObjects> maps;
    private StatisticalFolder statisticalFolder;
    private String outputPath;
    public static int NumberOfSensors;

    //TODO use statisticalFolder.increaseNumLandmarks before we add a new landmark to the map
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance;

        private static void init(StatisticalFolder statisticalFolder, String outputPath) {
            instance = new FusionSlam(statisticalFolder, outputPath);
        }
    }

    public synchronized static void addNumberOfSensors() {
        FusionSlam.NumberOfSensors = FusionSlam.NumberOfSensors + 1;
        System.out.println("@numSensors "+FusionSlam.NumberOfSensors);
    }

    public synchronized static void decreaseNumberOfSensors() {
        FusionSlam.NumberOfSensors= FusionSlam.NumberOfSensors -1;
        System.out.println("@numSensors "+FusionSlam.NumberOfSensors);
    }

    public synchronized static int getNumberOfSensors() {
        return FusionSlam.NumberOfSensors;
    }

    private FusionSlam(StatisticalFolder statisticalFolder, String outputPath) {
        trackedObjectsevents = new ArrayList<>();
        poses = new ArrayList<>();
        landMarks = new ArrayList<>();
        maps = new ConcurrentHashMap<>();
        this.statisticalFolder = statisticalFolder;
        this.outputPath = outputPath;
    }

    public static FusionSlam getInstance(StatisticalFolder statisticalFolder, String outputPath) {
        if (FusionSlamHolder.instance == null) {
            synchronized (FusionSlamHolder.class) {
                if (FusionSlamHolder.instance == null) {
                    FusionSlamHolder.init(statisticalFolder, outputPath);
                }
            }
        }
        return FusionSlamHolder.instance;
    }

    public void updateMap(TrackedObjectsEvent trackedObjectsEvent) {
        boolean found = false;
        for (PoseEvent poseEvent : poses) {
            if (poseEvent.getPose().getTime() == trackedObjectsEvent.getTrackedObjects().get(0).getTime()) {
                updateMap(trackedObjectsEvent, poseEvent);
                found = true;
            }
        }
        if (!found) {
            trackedObjectsevents.add(trackedObjectsEvent);
        }
    }

    public void updatePose(PoseEvent p) {
        boolean found = false;
        for (TrackedObjectsEvent event : trackedObjectsevents) {
            if (p.getPose().getTime() == event.getTrackedObjects().get(0).getTime()) {
                updateMap(event, p);
                found = true;
            }
        }
        if (!found) {
            poses.add(p);
        }
    }

    public void updateMap(TrackedObjectsEvent trackedObjectsEvent, PoseEvent poseEvent) {
        List<LandMark> newLandMarks = new ArrayList<>();
        for (LandMark landMark : landMarks) {
            for (TrackedObject trackedObject : trackedObjectsEvent.getTrackedObjects()) {
                if (landMark.getId().equals(trackedObject.getId())) {
                    landMark.addPoint(convertToChargingStation(trackedObject.getCoordinates(),poseEvent.getPose()));
                } else {
                    List<CloudPoint> n=new LinkedList<>();
                    n.add(convertToChargingStation(trackedObject.getCoordinates(), poseEvent.getPose()));
                    newLandMarks.add(new LandMark(trackedObject.getId(), trackedObject.getDescription(),n));
                }
            }
        }
        if (!newLandMarks.isEmpty()) {
            landMarks.addAll(newLandMarks);
        }
    }

    public CloudPoint convertToChargingStation(CloudPoint cloudPoint, Pose p) {
        double newX,newY;
        double alpha=p.getYaw() * Math.PI / 180;
            newX = Math.cos(alpha)*cloudPoint.getX() - Math.sin(alpha)*cloudPoint.getY() + p.getX();
            newY = Math.sin(alpha)*cloudPoint.getY() - Math.cos(alpha)*cloudPoint.getX() + p.getY();
        return new CloudPoint(newX,newY);
    }

    public void createOutputFile(ErrorReport errorReport){
        Gson gson = new Gson();
        // Serialize each object to JSON
        String jsonFolder = gson.toJson(statisticalFolder);
        String jsonLand = gson.toJson(landMarks);
        String result;
        if(errorReport!=null){
            String jsonReport = gson.toJson(errorReport);
            result ="{\"Statistic Folder\":"+jsonFolder+",\"LandMarks\":"+jsonLand+",\"ErrorReprot\":"+jsonReport+"}";
        } else
            result ="{\"Statistic Folder\":"+jsonFolder+",\"LandMarks\":"+jsonLand+"}";
        FileHandelUtil.writeJson(result, this.outputPath+"/output_file.json");
    }

//    public String toStringMap(){
//        StringBuilder result = new StringBuilder("\"landMarks\":{");
//        for (LandMark landMark : landMarks) {
//            result.append(landMark.toString()).append(",");
//        }
//
//        if (result.length() > 0 && result.charAt(result.length() - 1) == ',') {
//            result.setLength(result.length() - 1);
//        }
//        result.append("}");
//        return result.toString();
//    }

    public List<PoseEvent> getPoses(){
        return poses;
    }


}

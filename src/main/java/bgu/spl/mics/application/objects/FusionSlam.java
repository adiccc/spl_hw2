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
import java.util.concurrent.atomic.AtomicInteger;

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
    public AtomicInteger NumberOfSensors;

    //TODO use statisticalFolder.increaseNumLandmarks before we add a new landmark to the map
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance=new FusionSlam(null,"");

        private static void init(StatisticalFolder statisticalFolder, String outputPath) {
            instance = new FusionSlam(statisticalFolder, outputPath);
        }
    }

    public void initInstance(StatisticalFolder statisticalFolder, String outputPath) {
        FusionSlamHolder.init(statisticalFolder, outputPath);
    }

    public void decreaseNumberOfSensors() {
        NumberOfSensors.decrementAndGet();
        System.out.println("@numSensors "+NumberOfSensors);
    }

    public void setNumberOfSensors(int numberOfSensors) {
        NumberOfSensors.set(numberOfSensors);
    }

    public boolean isLeftSensorOn() {
        return NumberOfSensors.intValue()>0;
    }

    public AtomicInteger getNumberOfSensors() {
        return NumberOfSensors;
    }

    private FusionSlam(StatisticalFolder statisticalFolder, String outputPath) {
        trackedObjectsevents = new ArrayList<>();
        poses = new ArrayList<>();
        landMarks = new ArrayList<>();
        maps = new ConcurrentHashMap<>();
        this.statisticalFolder = statisticalFolder;
        this.outputPath = outputPath;
        this.NumberOfSensors = new AtomicInteger(0);
    }

    public static FusionSlam getInstance() {
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
            poses.add(p);
        for (TrackedObjectsEvent event : trackedObjectsevents) {
            if (p.getPose().getTime() == event.getTrackedObjects().get(0).getTime()) {
                updateMap(event, p);
            }
        }
    }

    public List<LandMark> updateMap(TrackedObjectsEvent trackedObjectsEvent, PoseEvent poseEvent) {//change from void for tests
        List<LandMark> newLandMarks = new ArrayList<>();
        for(TrackedObject trackedObject : trackedObjectsEvent.getTrackedObjects()) {
            boolean found = false;
            for (LandMark landMark : landMarks) {
                if (landMark.getId().equals(trackedObject.getId())){
                    landMark.UpdatePoints(convertToChargingStation(trackedObject.getCoordinates(),poseEvent.getPose()));
                    found = true;
                }
            }
            if (!found) {
                List<CloudPoint> newLandmarkCoorination =new LinkedList<>();
                newLandmarkCoorination.addAll(convertToChargingStation(trackedObject.getCoordinates(), poseEvent.getPose()));
                newLandMarks.add(new LandMark(trackedObject.getId(), trackedObject.getDescription(), newLandmarkCoorination));
            }
        }
        if (!newLandMarks.isEmpty()) {
            System.out.println("numLandMark "+newLandMarks.size());
            landMarks.addAll(newLandMarks);
            statisticalFolder.increaseNumLandmarks(newLandMarks.size());
            return newLandMarks;
        }
        return null;
    }
    public List<LandMark> landMarkList(){
        return landMarks;
    }
    public List<CloudPoint> convertToChargingStation(List<CloudPoint> cloudPoints, Pose p) {
        double alpha=p.getYaw() * Math.PI / 180;
        List<CloudPoint> chargingStationCo = new LinkedList<>();
        for(CloudPoint c: cloudPoints) {
             double newX = Math.cos(alpha)*c.getX() - Math.sin(alpha)*c.getY() + p.getX();
             double newY = Math.sin(alpha)*c.getX() + Math.cos(alpha)*c.getY() + p.getY();
             chargingStationCo.add(new CloudPoint(newX, newY));
        }
        return chargingStationCo;
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

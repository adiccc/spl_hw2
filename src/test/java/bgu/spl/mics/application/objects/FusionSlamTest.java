package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {
    PoseEvent poseEvent1;
    PoseEvent poseEvent2;
    TrackedObjectsEvent trackedObjectsEvent1;
    TrackedObjectsEvent trackedObjectsEvent2;
    FusionSlam fusionSlam;
    @BeforeEach
    void setUp() {
        poseEvent1=new PoseEvent(new Pose(1,2,15,1));
        List<TrackedObject> trackedObjects1=new ArrayList<TrackedObject>();
        List<CloudPoint> applePos=new ArrayList<>();
        applePos.add(new CloudPoint(1,2,0.104));
        applePos.add(new CloudPoint(1.2,2.2,0.104));
        trackedObjects1.add(new TrackedObject("apple",1,"green apple",applePos));
        trackedObjectsEvent1=new TrackedObjectsEvent(trackedObjects1);
        poseEvent2=new PoseEvent(new Pose(1,1,180,2));
        List<TrackedObject> trackedObjects2=new ArrayList<TrackedObject>();
        List<CloudPoint> bananaPos=new ArrayList<>();
        bananaPos.add(new CloudPoint(-2,1,0.104));
        bananaPos.add(new CloudPoint(-1.7,0.8,0.104));
        trackedObjects2.add(new TrackedObject("banana",2,"yellow banana",bananaPos));
        List<CloudPoint> orangePos=new ArrayList<>();
        orangePos.add(new CloudPoint(-3,2,0.104));
        orangePos.add(new CloudPoint(-1.7,0.8,0.104));
        trackedObjects2.add(new TrackedObject("orange",2,"orange orange",orangePos));
        trackedObjectsEvent2=new TrackedObjectsEvent(trackedObjects1);
        StatisticalFolder statisticalFolder = new StatisticalFolder();
        fusionSlam=FusionSlam.getInstance(statisticalFolder,"example_input");
    }

    @Test
    void updateMap() {
        List<LandMark> landmarks1=fusionSlam.updateMap(trackedObjectsEvent1,poseEvent1);
        List<LandMark> landmarks2=fusionSlam.updateMap(trackedObjectsEvent2,poseEvent2);
        for(LandMark landmark:landmarks1){
            if(landmark.getId().equals(landmark.getId())){

            }
        }
    }

    @Test
    void convertToChargingStation() {
    }
}
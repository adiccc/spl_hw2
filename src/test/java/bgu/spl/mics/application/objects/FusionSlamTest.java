package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.services.FusionSlamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {
    PoseEvent poseEventapple1;
    PoseEvent poseEvent2;
    PoseEvent poseEventapple2;
    TrackedObjectsEvent trackedObjectsEvent1;
    TrackedObjectsEvent trackedObjectsEvent2;
    TrackedObjectsEvent trackedObjectsEventapple2;
    FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        poseEventapple1=new PoseEvent(new Pose(1,2,45,1));
        List<TrackedObject> trackedObjects1=new ArrayList<TrackedObject>();
        List<CloudPoint> applePos=new ArrayList<>();
        applePos.add(new CloudPoint(1,2,0.104));
        applePos.add(new CloudPoint(2,2,0.104));
        trackedObjects1.add(new TrackedObject("apple",1,"green apple",applePos));
        trackedObjectsEvent1=new TrackedObjectsEvent(trackedObjects1);
        StatisticalFolder statisticalFolder = new StatisticalFolder();
        FusionSlam.getInstance().initInstance(statisticalFolder,"example_input");
        fusionSlam=FusionSlam.getInstance();

        poseEventapple2=new PoseEvent(new Pose(0,1,30,3));
        List<TrackedObject> trackedObjectsapple2=new ArrayList<TrackedObject>();
        List<CloudPoint> applePos2=new ArrayList<>();
        applePos2.add(new CloudPoint(2,3,0.104));
        applePos2.add(new CloudPoint(2.25,3.18,0.104));
        applePos2.add(new CloudPoint(1.5,2.25,0.104));
        trackedObjectsapple2.add(new TrackedObject("apple",1,"green apple",applePos2));
        trackedObjectsEventapple2=new TrackedObjectsEvent(trackedObjectsapple2);
    }

    @Test
    void updateMap() {
        List<LandMark> landMarkApple1=fusionSlam.updateMap(trackedObjectsEvent1,poseEventapple1);
        //List<LandMark> landmarks2=fusionSlam.updateMap(trackedObjectsEvent2,poseEvent2);
        List<CloudPoint> cloudPointsApple=new ArrayList<>();
        cloudPointsApple.add(new CloudPoint(0.29289321881345265,1.2928932188134523));
        cloudPointsApple.add(new CloudPoint(1.0000000000000002,1.9999999999999998));
        assertTrue(cloudPointsApple.get(0).equals(landMarkApple1.get(0).getCoordinates().get(0)));
        assertTrue(cloudPointsApple.get(1).equals(landMarkApple1.get(0).getCoordinates().get(1)));
        fusionSlam.updateMap(trackedObjectsEventapple2,poseEventapple2);
        List<LandMark> landMarkApple2 = fusionSlam.landMarkList();
        List<CloudPoint> cloudPointsApple2=new ArrayList<>();
        cloudPointsApple2.add(new CloudPoint(0.26247201319116514,0.34740850373006815));
        cloudPointsApple2.add(new CloudPoint(0.6792785792574937,0.6855196079827421));
        cloudPointsApple2.add(new CloudPoint(0.17403810567665823,-0.198557158514987));
        assertTrue(cloudPointsApple2.get(0).equals(landMarkApple2.get(0).getCoordinates().get(0)));
        assertTrue(cloudPointsApple2.get(1).equals(landMarkApple2.get(0).getCoordinates().get(1)));
        assertTrue(cloudPointsApple2.get(2).equals(landMarkApple2.get(0).getCoordinates().get(2)));
    }
}
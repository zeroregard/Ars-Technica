package com.zeroregard.ars_technica.ponder;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;

public class SourceMotorScenes {
    public static void usage(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("source_motor", "Using the Source Motor");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection sourceMotor = util.select().position(2, 1, 2);
        Selection sourceJar = util.select().position(3, 1, 2);
        Selection cog = util.select().position(1, 1, 2);

        scene.idle(15);

        scene.world().showSection(sourceMotor, Direction.DOWN);

        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("This block can be used to convert source into rotational force.")
                .pointAt(sourceMotor.getCenter())
                .placeNearTarget();

        scene.idle(60);


        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("It must be supplied with a nearby source jar.")
                .pointAt(sourceJar.getCenter())
                .placeNearTarget();

        scene.world().showSection(sourceJar, Direction.DOWN);
        scene.idle(60);

        scene.rotateCameraY(90);
        scene.world().showSection(cog, Direction.EAST);

        scene.idle(60);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Adjust the RPM on the sides of the motor, right-click elsewhere to adjust the SU-to-RPM ratio")
                .pointAt(sourceMotor.getCenter())
                .placeNearTarget();

        scene.idle(100);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Adjusting the SU-to-RPM ratio will conversely adjust the amount of source required")
                .pointAt(sourceMotor.getCenter())
                .placeNearTarget();

        scene.idle(100);
    }
}
package frc.lib.bluecrew.util;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Constants;

import java.util.Arrays;

import static frc.robot.Constants.FieldCoordinates.BLUE_SPEAKER;

public class FieldState implements Constants.GameStateConstants {

    private static FieldState instance;

    private boolean[] centerNotesExist;
    private int centerNoteIndex;
    private boolean centerNotesGone;
    private boolean onRedAlliance;

    private Translation3d speakerCoords = BLUE_SPEAKER;
    private Translation3d actualSpeakerCoords = BLUE_SPEAKER;

    private FieldState() {
        centerNotesExist = new boolean[]{
                true,
                true,
                true,
                true,
                true
        };

        centerNotesGone = false;
        centerNoteIndex = 0;

        var alliance = DriverStation.getAlliance();
        onRedAlliance = alliance.filter(value -> value == DriverStation.Alliance.Red).isPresent();
    }

    public static synchronized FieldState getInstance() {
        if (instance == null) {
            instance = new FieldState();
        }
        return instance;
    }

    public boolean[] getCenterNotesExist() {
        return centerNotesExist;
    }

    public void setCenterNotesExist(boolean[] centerNotesExist) {
        this.centerNotesExist = centerNotesExist;
    }

    public void setCenterNoteExists(int index, boolean val) {
        if (index < centerNotesExist.length) centerNotesExist[index] = val;
    }

    public void setCenterNoteIndex(int centerNoteIndex) {
        this.centerNoteIndex = centerNoteIndex;
    }

    public int getCenterNoteIndex() {
        return centerNoteIndex;
    }

    public void setCenterNotesGone(boolean centerNotesGone) {
        this.centerNotesGone = centerNotesGone;
    }

    public boolean isCenterNotesGone() {
        return centerNotesGone;
    }

    public boolean onRedAlliance() {
        return onRedAlliance;
    }

    public void setOnRedAlliance(boolean onRedAlliance) {
        this.onRedAlliance = onRedAlliance;
    }

    public Translation3d getSpeakerCoords() {
        return speakerCoords;
    }

    public void setSpeakerCoords(Translation3d speakerCoords) {
        this.speakerCoords = speakerCoords;
    }

    public Translation3d getActualSpeakerCoords() {
        return actualSpeakerCoords;
    }

    public void setActualSpeakerCoords(Translation3d actualSpeakerCoords) {
        this.actualSpeakerCoords = actualSpeakerCoords;
    }
}

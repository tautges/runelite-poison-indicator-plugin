package com.poisonednpcs.mocks;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

// Do not generally do this. Much better to mock. Not entirely certain I can bring in my own libraries, though, so not
// risking it.
public class MockNPC implements NPC {

    private Supplier<Integer> getIndexFn;
    private Supplier<Boolean> isDeadFn;

    private MockNPC() {}

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isInteracting() {
        return false;
    }

    @Override
    public Actor getInteracting() {
        return null;
    }

    @Override
    public int getHealthRatio() {
        return 0;
    }

    @Override
    public int getHealthScale() {
        return 0;
    }

    @Override
    public WorldPoint getWorldLocation() {
        return null;
    }

    @Override
    public LocalPoint getLocalLocation() {
        return null;
    }

    @Override
    public int getOrientation() {
        return 0;
    }

    @Override
    public int getCurrentOrientation() {
        return 0;
    }

    @Override
    public int getAnimation() {
        return 0;
    }

    @Override
    public int getPoseAnimation() {
        return 0;
    }

    @Override
    public void setPoseAnimation(int i) {

    }

    @Override
    public int getPoseAnimationFrame() {
        return 0;
    }

    @Override
    public void setPoseAnimationFrame(int i) {

    }

    @Override
    public int getIdlePoseAnimation() {
        return 0;
    }

    @Override
    public void setIdlePoseAnimation(int i) {

    }

    @Override
    public int getIdleRotateLeft() {
        return 0;
    }

    @Override
    public void setIdleRotateLeft(int i) {

    }

    @Override
    public int getIdleRotateRight() {
        return 0;
    }

    @Override
    public void setIdleRotateRight(int i) {

    }

    @Override
    public int getWalkAnimation() {
        return 0;
    }

    @Override
    public void setWalkAnimation(int i) {

    }

    @Override
    public int getWalkRotateLeft() {
        return 0;
    }

    @Override
    public void setWalkRotateLeft(int i) {

    }

    @Override
    public int getWalkRotateRight() {
        return 0;
    }

    @Override
    public void setWalkRotateRight(int i) {

    }

    @Override
    public int getWalkRotate180() {
        return 0;
    }

    @Override
    public void setWalkRotate180(int i) {

    }

    @Override
    public int getRunAnimation() {
        return 0;
    }

    @Override
    public void setRunAnimation(int i) {

    }

    @Override
    public void setAnimation(int i) {

    }

    @Override
    public int getAnimationFrame() {
        return 0;
    }

    @Override
    public void setActionFrame(int i) {

    }

    @Override
    public void setAnimationFrame(int i) {

    }

    @Override
    public IterableHashTable<ActorSpotAnim> getSpotAnims() {
        return null;
    }

    @Override
    public boolean hasSpotAnim(int i) {
        return false;
    }

    @Override
    public void createSpotAnim(int i, int i1, int i2, int i3) {

    }

    @Override
    public void removeSpotAnim(int i) {

    }

    @Override
    public void clearSpotAnims() {

    }

    @Override
    public int getGraphic() {
        return 0;
    }

    @Override
    public void setGraphic(int i) {

    }

    @Override
    public int getGraphicHeight() {
        return 0;
    }

    @Override
    public void setGraphicHeight(int i) {

    }

    @Override
    public int getSpotAnimFrame() {
        return 0;
    }

    @Override
    public void setSpotAnimFrame(int i) {

    }

    @Override
    public Polygon getCanvasTilePoly() {
        return null;
    }

    @Nullable
    @Override
    public Point getCanvasTextLocation(Graphics2D graphics2D, String s, int i) {
        return null;
    }

    @Override
    public Point getCanvasImageLocation(BufferedImage bufferedImage, int i) {
        return null;
    }

    @Override
    public Point getCanvasSpriteLocation(SpritePixels spritePixels, int i) {
        return null;
    }

    @Override
    public Point getMinimapLocation() {
        return null;
    }

    @Override
    public int getLogicalHeight() {
        return 0;
    }

    @Override
    public Shape getConvexHull() {
        return null;
    }

    @Override
    public WorldArea getWorldArea() {
        return null;
    }

    @Override
    public String getOverheadText() {
        return null;
    }

    @Override
    public void setOverheadText(String s) {

    }

    @Override
    public int getOverheadCycle() {
        return 0;
    }

    @Override
    public void setOverheadCycle(int i) {

    }

    @Override
    public boolean isDead() {
        return isDeadFn != null && isDeadFn.get();
    }

    @Override
    public void setDead(boolean b) {

    }

    @Override
    public WorldView getWorldView() {
        return null;
    }

    @Override
    public int getCombatLevel() {
        return 0;
    }

    @Override
    public int getIndex() {
        return getIndexFn == null ? 0 : getIndexFn.get();
    }

    @Override
    public NPCComposition getComposition() {
        return null;
    }

    @Nullable
    @Override
    public NPCComposition getTransformedComposition() {
        return null;
    }

    @Nullable
    @Override
    public NpcOverrides getModelOverrides() {
        return null;
    }

    @Nullable
    @Override
    public NpcOverrides getChatheadOverrides() {
        return null;
    }

    @Override
    public Model getModel() {
        return null;
    }

    @Override
    public int getModelHeight() {
        return 0;
    }

    @Override
    public void setModelHeight(int i) {

    }

    @Override
    public Node getNext() {
        return null;
    }

    @Override
    public Node getPrevious() {
        return null;
    }

    @Override
    public long getHash() {
        return 0;
    }

    public static Setup newSetup() {
        return new Setup();
    }

    public static class Setup {

        private final MockNPC mockNPC = new MockNPC();

        private Setup() {}

        public Setup getIndexFn(Supplier<Integer> getIndexFn) {
            this.mockNPC.getIndexFn = getIndexFn;
            return this;
        }

        public Setup setIsDeadFn(Supplier<Boolean> isDeadFn) {
            this.mockNPC.isDeadFn = isDeadFn;
            return this;
        }

        public MockNPC get() {
            return this.mockNPC;
        }
    }
}

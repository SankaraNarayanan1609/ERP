package com.Vcidex.StoryboardSystems.Inventory.POJO;

import java.util.List;
import java.util.Map;

public class MaterialInwardData {
    private String dcNo;
    private String grnDate;         // "DD-MM-YYYY"
    private String expectedDate;    // "DD-MM-YYYY"
    private String dispatchMode;
    private String trackingNo;
    private String noOfBoxes;
    private List<String> filePaths;
    private Map<Integer, String> receivedQtyByRow;

    // ─── Getters & Setters ──────────────────────────────────────────────────
    public String getDcNo() { return dcNo; }
    public void setDcNo(String dcNo) { this.dcNo = dcNo; }

    public String getGrnDate() { return grnDate; }
    public void setGrnDate(String grnDate) { this.grnDate = grnDate; }

    public String getExpectedDate() { return expectedDate; }
    public void setExpectedDate(String expectedDate) { this.expectedDate = expectedDate; }

    public String getDispatchMode() { return dispatchMode; }
    public void setDispatchMode(String dispatchMode) { this.dispatchMode = dispatchMode; }

    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }

    public String getNoOfBoxes() { return noOfBoxes; }
    public void setNoOfBoxes(String noOfBoxes) { this.noOfBoxes = noOfBoxes; }

    public List<String> getFilePaths() { return filePaths; }
    public void setFilePaths(List<String> filePaths) { this.filePaths = filePaths; }

    public Map<Integer, String> getReceivedQtyByRow() { return receivedQtyByRow; }
    public void setReceivedQtyByRow(Map<Integer, String> receivedQtyByRow) {
        this.receivedQtyByRow = receivedQtyByRow;
    }

    // ─── Builder-style methods ─────────────────────────────────────────────
    public MaterialInwardData withDcNo(String dcNo) {
        setDcNo(dcNo); return this;
    }
    public MaterialInwardData withGrnDate(String grnDate) {
        setGrnDate(grnDate); return this;
    }
    public MaterialInwardData withExpectedDate(String expectedDate) {
        setExpectedDate(expectedDate); return this;
    }
    public MaterialInwardData withDispatchMode(String dispatchMode) {
        setDispatchMode(dispatchMode); return this;
    }
    public MaterialInwardData withTrackingNo(String trackingNo) {
        setTrackingNo(trackingNo); return this;
    }
    public MaterialInwardData withNoOfBoxes(String noOfBoxes) {
        setNoOfBoxes(noOfBoxes); return this;
    }
    public MaterialInwardData withFilePaths(List<String> filePaths) {
        setFilePaths(filePaths); return this;
    }
    public MaterialInwardData withReceivedQtyByRow(Map<Integer,String> map) {
        setReceivedQtyByRow(map); return this;
    }
}
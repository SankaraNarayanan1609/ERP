// File: src/main/java/com/Vcidex/StoryboardSystems/Inventory/POJO/MaterialInwardData.java
package com.Vcidex.StoryboardSystems.Inventory.POJO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MaterialInwardData {
    private String dcNo;
    private LocalDate grnDate;         // changed from String to LocalDate
    private LocalDate expectedDate;    // changed from String to LocalDate
    private String dispatchMode;
    private String trackingNo;
    private String noOfBoxes;
    private List<String> filePaths;
    private Map<Integer, String> receivedQtyByRow;

    // ─── Getters & Setters ──────────────────────────────────────────────────
    public String getDcNo() {
        return dcNo;
    }
    public void setDcNo(String dcNo) {
        this.dcNo = dcNo;
    }

    public LocalDate getGrnDate() {
        return grnDate;
    }
    public void setGrnDate(LocalDate grnDate) {
        this.grnDate = grnDate;
    }

    public LocalDate getExpectedDate() {
        return expectedDate;
    }
    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getDispatchMode() {
        return dispatchMode;
    }
    public void setDispatchMode(String dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public String getTrackingNo() {
        return trackingNo;
    }
    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getNoOfBoxes() {
        return noOfBoxes;
    }
    public void setNoOfBoxes(String noOfBoxes) {
        this.noOfBoxes = noOfBoxes;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }
    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public Map<Integer, String> getReceivedQtyByRow() {
        return receivedQtyByRow;
    }
    public void setReceivedQtyByRow(Map<Integer, String> receivedQtyByRow) {
        this.receivedQtyByRow = receivedQtyByRow;
    }

    // ─── Builder-style methods ─────────────────────────────────────────────
    public MaterialInwardData withDcNo(String dcNo) {
        setDcNo(dcNo);
        return this;
    }
    public MaterialInwardData withGrnDate(LocalDate grnDate) {
        setGrnDate(grnDate);
        return this;
    }
    public MaterialInwardData withExpectedDate(LocalDate expectedDate) {
        setExpectedDate(expectedDate);
        return this;
    }
    public MaterialInwardData withDispatchMode(String dispatchMode) {
        setDispatchMode(dispatchMode);
        return this;
    }
    public MaterialInwardData withTrackingNo(String trackingNo) {
        setTrackingNo(trackingNo);
        return this;
    }
    public MaterialInwardData withNoOfBoxes(String noOfBoxes) {
        setNoOfBoxes(noOfBoxes);
        return this;
    }
    public MaterialInwardData withFilePaths(List<String> filePaths) {
        setFilePaths(filePaths);
        return this;
    }
    public MaterialInwardData withReceivedQtyByRow(Map<Integer, String> map) {
        setReceivedQtyByRow(map);
        return this;
    }
}
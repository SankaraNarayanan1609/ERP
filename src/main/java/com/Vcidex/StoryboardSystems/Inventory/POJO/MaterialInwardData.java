// File: src/main/java/com/Vcidex/StoryboardSystems/Inventory/POJO/MaterialInwardData.java

/**
 * POJO representing data required for Material Inward (GRN) entry.
 * This includes delivery details, uploaded documents, and received quantities.
 */
package com.Vcidex.StoryboardSystems.Inventory.POJO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MaterialInwardData {

    // Document-level details
    private String dcNo;              // Delivery challan number (e.g., DC20240618123)
    private LocalDate grnDate;        // GRN date (when goods were received)
    private LocalDate expectedDate;   // Expected delivery date
    private String dispatchMode;      // Dispatch method (e.g., "Fedex")
    private String trackingNo;        // Shipment tracking number
    private String noOfBoxes;         // How many boxes/packages received

    // Uploads & file attachments
    private List<String> filePaths;   // Paths to uploaded documents (PDFs, images)

    // Line-wise quantity mapping — key = row index, value = quantity received
    private Map<Integer, String> receivedQtyByRow;

    // ─── Getters & Setters — Standard JavaBean Methods ─────────────────────

    public String getDcNo() { return dcNo; }
    public void setDcNo(String dcNo) { this.dcNo = dcNo; }

    public LocalDate getGrnDate() { return grnDate; }
    public void setGrnDate(LocalDate grnDate) { this.grnDate = grnDate; }

    public LocalDate getExpectedDate() { return expectedDate; }
    public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }

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

    // ─── Builder-style Methods — for chaining fields fluently ──────────────

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
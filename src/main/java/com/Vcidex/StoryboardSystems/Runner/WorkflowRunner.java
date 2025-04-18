//package com.Vcidex.StoryboardSystems.Runner;
//
////import com.Vcidex.StoryboardSystems.Common.Workflow.FeatureToggleService;
////import com.Vcidex.StoryboardSystems.Common.Workflow.Approval.ApprovalProcessor;
//import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseService;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class WorkflowRunner {
//    public static void main(String[] args) {
//        // ✅ Fetch all pending approval clients dynamically
//        Map<String, List<String>> clientWorkflows = DatabaseService.getPendingApprovalClients();
//
//        // ✅ Extract all client IDs
//        Set<String> clientIDs = clientWorkflows.keySet();
//
//        // ✅ Load approval rules dynamically for each client
//        for (String clientID : clientIDs) {
//            FeatureToggleService.loadApprovalChainsFromDB(clientID);
//        }
//
//        // ✅ Iterate over all clients & process their workflows dynamically
//        for (Map.Entry<String, List<String>> entry : clientWorkflows.entrySet()) {
//            String clientID = entry.getKey();
//            List<String> requestTypes = entry.getValue();
//
//            for (String requestType : requestTypes) {
//                ApprovalProcessor.startApprovalProcess(clientID, requestType);
//            }
//        }
//    }
//}
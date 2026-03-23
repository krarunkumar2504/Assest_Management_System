package com.asset.asset_management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AIService {



    public String getRecommendation(String prompt) {

        try {
            int purchaseCost = extractNumber(prompt, "Purchase Cost:");
            int currentValue = extractNumber(prompt, "Current Value:");
            int maintenanceCost = extractNumber(prompt, "Maintenance Cost:");

            double ratio = currentValue == 0 ? 0 : (double) maintenanceCost / currentValue;

            String assetType = extractText(prompt, "Type:");
            String assetName = extractText(prompt, "Asset Name:");

            StringBuilder response = new StringBuilder();

            java.util.Random rand = new java.util.Random();

            // 🎯 Dynamic intro messages
            String[] intros = {
                    "🔍 AI Analysis for ",
                    "🤖 Smart Insight for ",
                    "📊 Asset Evaluation: ",
                    "⚙️ System Review for "
            };

            response.append(intros[rand.nextInt(intros.length)])
                    .append(assetName)
                    .append(":\n\n");

            // 🎯 Analysis
            response.append("• Current Value: ₹").append(currentValue).append("\n");
            response.append("• Maintenance Cost: ₹").append(maintenanceCost).append("\n\n");

            // 🎯 Decision + variation
            if (ratio < 0.3) {

                String[] repairMsgs = {
                        "✅ Recommendation: Repair the asset.\n\n💡 Reason: Cost is very low compared to value.",
                        "✔ Suggested Action: Proceed with repair.\n\n💡 Reason: Economically efficient.",
                        "🛠️ Best Option: Repair.\n\n💡 Reason: Maintenance cost is minimal."
                };

                response.append(repairMsgs[rand.nextInt(repairMsgs.length)]);

            } else if (ratio < 0.6) {

                String[] cautionMsgs = {
                        "⚠️ Recommendation: Repair with caution.\n\n💡 Reason: Cost is moderate.",
                        "⚠️ Suggested: Repair but monitor closely.\n\n💡 Risk: Future expenses may increase.",
                        "🟡 Balanced Decision: Repair possible.\n\n💡 Warning: Keep track of recurring costs."
                };

                response.append(cautionMsgs[rand.nextInt(cautionMsgs.length)]);

            } else {

                String[] replaceMsgs = {
                        "❌ Recommendation: Replace the asset.\n\n💡 Reason: Maintenance cost is too high.",
                        "🚨 Action Needed: Replace immediately.\n\n💡 Risk: Continued repair is inefficient.",
                        "🔴 Best Decision: Replacement.\n\n💡 Reason: High maintenance burden."
                };

                response.append(replaceMsgs[rand.nextInt(replaceMsgs.length)]);
            }

            // 🎯 Risk level
            response.append("\n\n📉 Risk Level: ");
            if (ratio < 0.3) response.append("Low");
            else if (ratio < 0.6) response.append("Medium");
            else response.append("High");

            // 🎯 Asset-specific intelligence
            if (assetType != null) {
                String type = assetType.toLowerCase();

                if (type.contains("it") || type.contains("laptop")) {
                    response.append("\n🖥️ Insight: IT assets lose value quickly. Upgrade planning is important.");
                } else if (type.contains("vehicle")) {
                    response.append("\n🚗 Insight: Vehicles may incur repeated costs. Consider lifecycle cost.");
                } else if (type.contains("equipment")) {
                    response.append("\n🏭 Insight: Preventive maintenance improves lifespan.");
                } else {
                    response.append("\n📦 Insight: Regular monitoring can reduce unexpected failures.");
                }
            }

            // 🎯 Final advice variation
            String[] endings = {
                    "\n\n📊 Final Advice: Compare long-term repair vs replacement cost.",
                    "\n\n📊 Recommendation Summary: Evaluate ROI before decision.",
                    "\n\n📊 Strategic Tip: Focus on long-term savings."
            };

            response.append(endings[rand.nextInt(endings.length)]);

            return response.toString();

        } catch (Exception e) {
            return "⚠️ AI Error: Unable to analyze asset data.";
        }
    }    private int extractNumber(String text, String key) {
        try {
            int start = text.indexOf(key) + key.length();
            int end = text.indexOf("\n", start);
            return Integer.parseInt(text.substring(start, end).trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String extractText(String text, String key) {
        try {
            int start = text.indexOf(key) + key.length();
            int end = text.indexOf("\n", start);
            return text.substring(start, end).trim();
        } catch (Exception e) {
            return "";
        }
    }
}
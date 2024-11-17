//package org.vivacon;
//
//public class TemporalExpressionFactory {
//    public static TimeExpression fromJson(JsonNode node) {
//        String type = node.get("type").asText();
//        switch (type) {
//            case "union":
//                return new UnionRule(parseChildRules(node.get("rules")));
//            case "intersection":
//                return new IntersectionRule(parseChildRules(node.get("rules")));
//            case "exclusion":
//                return new ExclusionRule(parseChildRules(node.get("rules")));
//            case "weekly":
//                return new WeeklyRule(parseDays(node.get("days")), parseTimeRange(node.get("time_range")));
//            case "monthly":
//                return new MonthlyRule(parseDays(node.get("days")), parseMonths(node.get("months")));
//            case "yearly":
//                return new YearlyRule(parseDates(node.get("dates")));
//            case "custom":
//                return new CustomRule(node.get("logic"));
//            default:
//                throw new IllegalArgumentException("Unknown rule type: " + type);
//        }
//    }
//
//    private static List<TimeExpression> parseChildRules(JsonNode rulesNode) {
//        List<TimeExpression> rules = new ArrayList<>();
//        for (JsonNode child : rulesNode) {
//            rules.add(fromJson(child));
//        }
//        return rules;
//    }
//
//    private static Set<Integer> parseDays(JsonNode daysNode) {
//        Set<Integer> days = new HashSet<>();
//        if (daysNode != null) {
//            for (JsonNode day : daysNode) {
//                days.add(day.asInt());
//            }
//        }
//        return days;
//    }
//
//    private static TimeRange parseTimeRange(JsonNode rangeNode) {
//        if (rangeNode == null) return null;
//        return new TimeRange(rangeNode.get("start").asText(), rangeNode.get("end").asText());
//    }
//
//    private static Set<Integer> parseMonths(JsonNode monthsNode) {
//        Set<Integer> months = new HashSet<>();
//        if (monthsNode != null) {
//            for (JsonNode month : monthsNode) {
//                months.add(month.asInt());
//            }
//        }
//        return months;
//    }
//
//    private static Set<LocalDate> parseDates(JsonNode datesNode) {
//        Set<LocalDate> dates = new HashSet<>();
//        if (datesNode != null) {
//            for (JsonNode date : datesNode) {
//                dates.add(LocalDate.parse(date.asText()));
//            }
//        }
//        return dates;
//    }
//}
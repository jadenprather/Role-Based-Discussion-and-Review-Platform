package review;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ReviewService {

    // ---- Feedback model ----
    public static class Feedback {
        public enum Scope { PRIVATE_TO_STUDENT, STAFF_ONLY }
        public enum TargetType { POST, REPLY }

        private final int id;
        private final TargetType targetType;
        private final int targetId;
        private final String fromStaff;   // username of staff
        private final String toStudent;   // username of student
        private final String text;
        private final Scope scope;
        private final LocalDateTime createdAt = LocalDateTime.now();

        public Feedback(int id, TargetType tt, int tid, String from, String to, String text, Scope s){
            this.id=id; this.targetType=tt; this.targetId=tid; this.fromStaff=from;
            this.toStudent=to; this.text=text; this.scope=s;
        }
        public int getId(){ return id; }
        public TargetType getTargetType(){ return targetType; }
        public int getTargetId(){ return targetId; }
        public String getFromStaff(){ return fromStaff; }
        public String getToStudent(){ return toStudent; }
        public String getText(){ return text; }
        public Scope getScope(){ return scope; }
        public LocalDateTime getCreatedAt(){ return createdAt; }

        @Override public String toString(){
            return "["+createdAt+"] "+fromStaff+" → "+toStudent+" ("+scope+"): "+text;
        }
    }

    // ---- Parameter model ----
    public static class Parameter {
        private final int id;
        private String name;
        private String description;
        private int maxPoints;
        private double weight; // 0..1 (optional)

        public Parameter(int id, String name, String description, int maxPoints, double weight){
            this.id=id; this.name=name; this.description=description;
            this.maxPoints=maxPoints; this.weight=weight;
        }
        public int getId(){ return id; }
        public String getName(){ return name; }
        public String getDescription(){ return description; }
        public int getMaxPoints(){ return maxPoints; }
        public double getWeight(){ return weight; }

        public void setName(String n){ name=n; }
        public void setDescription(String d){ description=d; }
        public void setMaxPoints(int m){ maxPoints=m; }
        public void setWeight(double w){ weight=w; }

        @Override public String toString(){
            return name+" (max "+maxPoints+", wt "+weight+")";
        }
		public Object getAuthor() {
			// TODO Auto-generated method stub
			return null;
		}
    }

    // ---- Stores ----
    private final AtomicInteger fbId = new AtomicInteger(1);
    private final AtomicInteger paramId = new AtomicInteger(1);
    private final Map<Integer, Feedback> feedbacks = new LinkedHashMap<>();
    private final Map<Integer, Parameter> parameters = new LinkedHashMap<>();

    // ---- Feedback API ----
    public Feedback addFeedback(Feedback.TargetType tt, int targetId, String fromStaff,
                                String toStudent, String text, Feedback.Scope scope){
        int id = fbId.getAndIncrement();
        Feedback f = new Feedback(id, tt, targetId, fromStaff, toStudent, text, scope);
        feedbacks.put(id, f);	
        return f;
    }

    public List<Feedback> listFeedbackForTarget(Feedback.TargetType tt, int targetId){
        List<Feedback> out = new ArrayList<>();
        for (var f : feedbacks.values()){
            if (f.getTargetType()==tt && f.getTargetId()==targetId) out.add(f);
        }
        return out;
    }

    // ---- Parameter API (CRUD) ----
    public Parameter createParameter(String name, String desc, int maxPoints, double weight){
        int id = paramId.getAndIncrement();
        Parameter p = new Parameter(id, name, desc, maxPoints, weight);
        parameters.put(id, p);
        return p;
    }
    public List<Parameter> listParameters(){ return new ArrayList<>(parameters.values()); }
    public boolean deleteParameter(int id){ return parameters.remove(id)!=null; }
    public Parameter getParameter(int id){ return parameters.get(id); }
}

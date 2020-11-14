package aiad.agentbehaviours;

        import aiad.Environment;
        import aiad.TrafficPoint;
        import aiad.access_point.FlyingAccessPoint;
        import jade.core.AID;
        import jade.core.behaviours.BaseInitiator;
        import jade.lang.acl.ACLMessage;
        import jade.proto.AchieveREInitiator;

        import java.util.ArrayList;
        import java.util.Vector;

public class TrafficPointRequestProtocolInit extends
        AchieveREInitiator {

    TrafficPoint trafficPoint;
    Environment env;
    Integer currentReceiverDrone;

    public TrafficPointRequestProtocolInit(TrafficPoint a, ACLMessage msg, Environment env, Integer currentReceiverDrone) {
        super(a, msg);
        this.trafficPoint = a;
        this.env = env;
        this.currentReceiverDrone = currentReceiverDrone;
    }

    @Override
    protected Vector prepareRequests(ACLMessage request) {
        System.out.println("Preparing request: " + trafficPoint.getTraffic());
        Vector v = new Vector();
        try {
            request.setContentObject(this.trafficPoint);
            ArrayList<FlyingAccessPoint> near_drones = env.getNearDrones(trafficPoint);
            request.addReceiver(new AID(near_drones.get(currentReceiverDrone).getLocalName(), false));
            v.add(request);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return v;
    }

    @Override
    protected void handleFailure(ACLMessage failure) {
        // Retry with new drone. TODO: make sure there are no synchronization errors, for instance another drone being added to the vector before we call this.
        //this.trafficPoint.addBehaviour(new TrafficPointRequestProtocolInit(this.trafficPoint, new ACLMessage(ACLMessage.CFP), this.env, this.currentReceiverDrone++));
        //super.handleFailure(refuse);
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {

        System.out.println("got " + responses.size() + " responses!");

        //TODO: choice algorithm
        for(int i=0; i<responses.size(); i++) {
            ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
            acceptances.add(msg);
        }
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        System.out.println("got " + resultNotifications.size() + " result notifs!");
    }

}
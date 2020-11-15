package aiad.agentbehaviours;

import aiad.Environment;
import aiad.TrafficPoint;
import aiad.access_point.AccessPoint;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class APRequestProtocolResponse extends AchieveREResponder {

    AccessPoint accessPoint;
    Environment env;

    public APRequestProtocolResponse(AccessPoint a, MessageTemplate mt, Environment env) {
        super(a, mt);
        this.accessPoint = a;
        this.env = env;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws RefuseException {
        System.out.println("(handleRequest) " + this.accessPoint.getLocalName() + " Received request: " + request);
        ACLMessage response = new ACLMessage(ACLMessage.AGREE);
        try {
            TrafficPoint tp = (TrafficPoint) request.getContentObject();
            System.out.println("Traffic point traffic: " + tp.getTraffic());
            this.accessPoint.addBehaviour(new APSubContractNetInit(this.accessPoint, tp, new ACLMessage(ACLMessage.CFP), this.env));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
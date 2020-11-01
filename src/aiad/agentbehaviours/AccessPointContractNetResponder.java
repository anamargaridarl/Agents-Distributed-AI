package aiad.agentbehaviours;

import aiad.Environment;
import aiad.access_point.AccessPoint;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class AccessPointContractNetResponder extends ContractNetResponder {

    AccessPoint accessPoint;
    Environment env;

    public AccessPointContractNetResponder(AccessPoint a, MessageTemplate mt, Environment env) {
        super(a, mt);
        this.accessPoint = a;
        this.env = env;
    }

    protected jade.lang.acl.ACLMessage handleCfp(jade.lang.acl.ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        System.out.println("FAP agent " + this.accessPoint.getLocalName() + ": CFP received from " + cfp.getSender().getName() + ". Action is " + cfp.getContent());
        boolean proposal = this.accessPoint.evaluateTrafficRequest();
        if (proposal) {
            System.out.println("FAP agent " + this.accessPoint.getLocalName() + ": Proposing " + this.accessPoint.getAvailableTraffic());
            jade.lang.acl.ACLMessage propose = cfp.createReply();
            propose.setPerformative(jade.lang.acl.ACLMessage.PROPOSE);
            propose.setContent(String.valueOf(this.accessPoint.getAvailableTraffic()));
            return propose;
        } else {
            System.out.println("FAP agent " + this.accessPoint.getLocalName() + ": Refused contract from " + cfp.getSender().getName());
            throw new RefuseException("proposal-refused");
        }
    }

    protected jade.lang.acl.ACLMessage handleAcceptProposal(jade.lang.acl.ACLMessage cfp, jade.lang.acl.ACLMessage propose, jade.lang.acl.ACLMessage accept) throws FailureException {
        System.out.println("FAP Agent " + this.accessPoint.getLocalName() + ": Proposal accepted");
        if (this.accessPoint.handleTrafficRequest()) {
            System.out.println("FAP Agent " + this.accessPoint.getLocalName() + ": Request accepted, connecting to Traffic Point");
            jade.lang.acl.ACLMessage inform = accept.createReply();
            inform.setPerformative(jade.lang.acl.ACLMessage.INFORM);
            return inform;
        } else {
            System.out.println("FAP Agent " + this.accessPoint.getLocalName() + ": Request denied, refusing connection");
            throw new FailureException("refused-traffic-request");
        }
    }

    protected void handleRejectProposal(jade.lang.acl.ACLMessage cfp, jade.lang.acl.ACLMessage propose, ACLMessage reject) {
        System.out.println("FAP Agent " + this.accessPoint.getLocalName() + ": Proposal rejected");
    }
}

package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import databases.DatabaseTA;
import edu.jhu.isi.grothsahai.api.impl.VerifierImpl;
import edu.jhu.isi.grothsahai.entities.CommonReferenceString;
import edu.jhu.isi.grothsahai.entities.Matrix;
import edu.jhu.isi.grothsahai.entities.Proof;
import edu.jhu.isi.grothsahai.entities.QuarticElement;
import edu.jhu.isi.grothsahai.entities.SingleProof;
import edu.jhu.isi.grothsahai.entities.Statement;
import edu.jhu.isi.grothsahai.entities.Vector;
import edu.jhu.isi.grothsahai.enums.ProblemType;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import utilities.Contact;
import utilities.ContactSigned;
import utilities.VectorPPP;

public class HealthAuthority {
	

	private final DatabaseTA databaseTA;
	private final Element secret_key_ha;
	private final Element public_key_ha;
	private final Element g2;
	private final Pairing param;
	private VerifierImpl verifier;
	private VectorPPP pppU;


	private final ExecutorService executor;

	public HealthAuthority(TrustedAuthority ta, CommonReferenceString crs) {

		this.databaseTA = ta.getDatabase();
		this.g2 = databaseTA.getG2();
		this.param = databaseTA.getParam();
		Vector keys = ta.ha_keygen(); 
		this.public_key_ha = keys.get(0);
		this.secret_key_ha = keys.get(1);
		this.verifier = new VerifierImpl(crs, ta.getExecutor());
		this.pppU = new VectorPPP(crs.getU1(), crs);


		this.executor = ta.getExecutor();

	}

	public Element[] set_userIDha() {
		Element tu = param.getZr().newRandomElement();
		Element hu = g2.duplicate().mulZn(tu);
		Element[] output = { hu, tu };
		return output;
	}

	public boolean Sig_VerifyHA(List<ContactSigned> contacts, CommonReferenceString crs) {
		for (int i = 0; i < contacts.size(); i++) {
			List<Statement> equations = contacts.get(i).getStatements();
			Proof[] proofs = contacts.get(i).getProofs();
			if (!Sig_Verify(equations, proofs, crs)) {
				return false;
			}
		}
		return true;
	}

	public boolean Sig_Verify_aggregation(List<ContactSigned> contacts, CommonReferenceString crs) {
		try {
			
			Element lhs_m = new QuarticElement(crs.getBT(), crs.getGT().newZeroElement(), crs.getGT().newZeroElement(),
					crs.getGT().newZeroElement(), crs.getGT().newZeroElement());
			Element rhs_m = new QuarticElement(crs.getBT(), crs.getGT().newZeroElement(), crs.getGT().newZeroElement(),
					crs.getGT().newZeroElement(), crs.getGT().newZeroElement());

			List<Future<Element>> ftr_lhs_m = new ArrayList<Future<Element>>();
			List<Future<Element>> ftr_rhs_m = new ArrayList<Future<Element>>();

			for (int i = 0; i < contacts.size(); i++) {

				Proof proof = contacts.get(i).getProofs()[0];
				Statement statement = contacts.get(i).getStatements().get(0);
				SingleProof singleProof = proof.getProofs().get(0);
				Proof proof2 = contacts.get(i).getProofs()[1];
				Statement statement2 = contacts.get(i).getStatements().get(1);
				SingleProof singleProof2 = proof2.getProofs().get(0);

				

				ftr_rhs_m.add(executor.submit(new PairingVectorPPP(pppU, singleProof.getPi(), crs)));

			ftr_rhs_m.add(executor.submit(new PairingVector(singleProof.getTheta(), crs.getU2(), crs))); 


				ftr_rhs_m.add(executor.submit(new PairingVectorPPP(pppU, singleProof2.getPi(), crs)));

			ftr_rhs_m.add(executor.submit(new PairingVector(singleProof2.getTheta(), crs.getU2(), crs)));


				ftr_lhs_m.add(executor
						.submit(new PairingVector(proof.getC(), statement.getGamma().multiply(proof.getD()), crs)));
				ftr_lhs_m.add(executor
						.submit(new PairingVector(proof2.getC(), statement2.getGamma().multiply(proof2.getD()), crs)));

			}

			for (int i = 0; i < ftr_lhs_m.size(); i++) {
				try {
					lhs_m = lhs_m.add(ftr_lhs_m.get(i).get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < ftr_rhs_m.size(); i++) {
				try {
					rhs_m = rhs_m.add(ftr_rhs_m.get(i).get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			Element lhs_p = new QuarticElement(crs.getBT(), crs.getGT().newZeroElement(), crs.getGT().newZeroElement(),
					crs.getGT().newZeroElement(), crs.getGT().newZeroElement());
			Element rhs_p = new QuarticElement(crs.getBT(), crs.getGT().newZeroElement(), crs.getGT().newZeroElement(),
					crs.getGT().newZeroElement(), crs.getGT().newZeroElement());

			Vector a;
			Vector b;
			Vector c;
			Vector d;
			Vector C_sum;
			Vector D_sum;
			Matrix gamma;

			List<Future<Element>> ftr_lhs_p = new ArrayList<Future<Element>>();
			List<Future<Element>> ftr_rhs_p = new ArrayList<Future<Element>>();

			for (int j = 2; j < 6; j++) {
				c = contacts.get(0).getProofs()[j].getC();
				d = contacts.get(0).getProofs()[j].getD();
				C_sum = Vector.getQuadraticZeroVector(c.get(0).getField(), param, c.getLength());
				D_sum = Vector.getQuadraticZeroVector(d.get(0).getField(), param, d.getLength());
				for (int i = 0; i < contacts.size(); i++) {
					d = contacts.get(i).getProofs()[j].getD();
					c = contacts.get(i).getProofs()[j].getC();

					D_sum = D_sum.add(d);
					C_sum = C_sum.add(c);

					gamma = contacts.get(i).getStatements().get(j).getGamma();

					ftr_lhs_p.add(executor.submit(new PairingVector(c, gamma.multiply(d), crs)));
				}
				a = contacts.get(0).getStatements().get(j).getA();
				b = contacts.get(0).getStatements().get(j).getB();
				
				ftr_lhs_p.add(executor.submit(new PairingVector(crs.iota(1, a), D_sum, crs)));
				ftr_lhs_p.add(executor.submit(new PairingVector(C_sum, crs.iota(2, b), crs)));

			}

			Vector pi = contacts.get(0).getProofs()[2].getProofs().get(0).getPi();

			Vector pi_sum = Vector.getQuadraticZeroVector(pi.get(0).getField(), param, pi.getLength());

			Vector theta = contacts.get(0).getProofs()[2].getProofs().get(0).getTheta();
			Vector theta_sum = Vector.getQuadraticZeroVector(theta.get(0).getField(), param, theta.getLength());

			Element t;

			for (int j = 2; j < 6; j++) {
				t = contacts.get(0).getStatements().get(j).getT();
				rhs_p = rhs_p.add(crs.iotaT(ProblemType.PAIRING_PRODUCT, t));

				for (int i = 0; i < contacts.size(); i++) {
					pi = contacts.get(i).getProofs()[j].getProofs().get(0).getPi();
					pi_sum = pi_sum.add(pi);
					theta = contacts.get(i).getProofs()[j].getProofs().get(0).getTheta();
					theta_sum = theta_sum.add(theta);

				}
			}


			ftr_rhs_p.add(executor.submit(new PairingVectorPPP(pppU, pi_sum, crs)));

		ftr_rhs_p.add(executor.submit(new PairingVector(theta_sum, crs.getU2(), crs)));

			if (!lhs_m.sub(rhs_m).isZero()) {
				return false;
			}

			for (int i = 0; i < ftr_lhs_p.size(); i++) {
				try {
					lhs_p = lhs_p.add(ftr_lhs_p.get(i).get());
				} catch (InterruptedException | ExecutionException e) {

					e.printStackTrace();
				}
			}
			for (int i = 0; i < ftr_rhs_p.size(); i++) {
				try {
					rhs_p = rhs_p.add(ftr_rhs_p.get(i).get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			return lhs_p.sub(rhs_p).isZero();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	public boolean Sig_Verify(List<Statement> equations, Proof[] proofs, CommonReferenceString crs) {
		/**
		 * Suit l'algorithme 4
		 */
		try {

			int len = equations.size();
			List<Statement> statement;
			List<Future<Boolean>> ftr_bools = new ArrayList<Future<Boolean>>();

			for (int i = 0; i < len; i++) {
				statement = new ArrayList<Statement>();
				statement.add(equations.get(i));

				
				ftr_bools.add(executor.submit(new Verif(verifier, statement, proofs[i])));

			}

			for (int i = 0; i < len; i++) {
				if (ftr_bools.get(i).get()) {
				} else {
					System.out.println("This is false");
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean CCM_Verify_aggregation(Server server, User user) {
		try {
		Element Y1 = server.getPublic_key_server().get(0).duplicate();
		Element Y2 = server.getPublic_key_server().get(1).duplicate();
		
		final Element tA = user.gettU().getImmutable();
		List<ContactSigned> contacts = user.getContact_list();
		
		Element Mi_sum = contacts.get(0).getM().duplicate();
		Element PSpi_sum = contacts.get(0).getPSp().duplicate();
		for (int i=1; i<contacts.size();i++) {
			Mi_sum.add(contacts.get(i).getM());
			PSpi_sum.add(contacts.get(i).getPSp());
		}
		
		Element lhs = Mi_sum;
		Element rhs = Y1.mulZn(tA.mulZn(PSpi_sum));
		
		rhs.add(Y2.mulZn(tA.mul(contacts.size())));
		

		
		return lhs.isEqual(rhs);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	public boolean CCM_VerifyHA(Server server, User user) {
		Element tA = user.gettU();
		List<ContactSigned> contacts = user.getContact_list();
		for (int i=0; i<user.getContact_list().size();i++) {
			
			Element M = contacts.get(i).getM();
			Element PSp = contacts.get(i).getPSp();
			
			boolean bool = CCM_Verify(server,M,PSp,tA);
			if (!bool) {
				return false;
			}
		}
		return true;
	}
	
	public boolean CCM_Verify(Server server, Element M, Element PSp, Element tA) {
		try {
			Element lhs = M.duplicate();

			Element Y1 = server.getPublic_key_server().get(0).getImmutable();
			Element Y2 = server.getPublic_key_server().get(1).getImmutable();
			
			Element rhs = Y1.duplicate();
			rhs.mulZn(tA);
			rhs.mulZn(PSp);
			
			rhs.add(Y2.mulZn(tA));


			return lhs.isEqual(rhs);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Element getPublic_key_ha() {
		return public_key_ha;
	}


	private class Verif implements Callable<Boolean> {
		private VerifierImpl verifier;
		private List<Statement> statement;
		private Proof proof;

		public Verif(VerifierImpl verifier, List<Statement> statement, Proof proof) {
			this.verifier = verifier;
			this.statement = statement;
			this.proof = proof;
		}

		@Override
		public Boolean call() throws Exception {
			return verifier.verify(statement, proof);
		}

	}

	private class PairingVector implements Callable<Element> {
		
		private Vector v1;
		private Vector v2;
		private CommonReferenceString crs;

		public PairingVector(Vector v1, Vector v2, CommonReferenceString crs) {
			this.v1 = v1;
			this.v2 = v2;
			this.crs = crs;
		}

		@Override
		public Element call() throws Exception {
			Element output = v1.pairInB(v2, crs.getPairing());
			return output;
		}

	}

	private class PairingVectorPPP implements Callable<Element> {
		
		private VectorPPP vppp;
		private Vector v2;
		private CommonReferenceString crs;

		public PairingVectorPPP(VectorPPP vppp, Vector v2, CommonReferenceString crs) {
			this.vppp = vppp;
			this.v2 = v2;
			this.crs = crs;
		}

		@Override
		public Element call() throws Exception {
			Element output = vppp.pairing(v2);
			return output;
		}
	}


}

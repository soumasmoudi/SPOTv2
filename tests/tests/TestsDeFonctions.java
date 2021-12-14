package tests; 

import entities.GroupManager;
import entities.HealthAuthority;
import entities.Proxy;
import entities.Server;
import entities.TrustedAuthority;
import entities.User;
import it.unisa.dia.gas.jpbc.Element;
import statistics.base.*;
import utilities.Contact;
import utilities.ContactSigned;

import org.junit.Test;
import edu.jhu.isi.grothsahai.entities.Proof;
import edu.jhu.isi.grothsahai.entities.Statement;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;

public class TestsDeFonctions {
	/**
	 * Teste chaque fonction à part avec de nouveaux paramètres et calcule son temps d'éxécution.
	 * Est utilisé pour vérifier le bon fonctionnement du système.
	 */

	private int nb_iter; //nombre d'itérations de chaque test

	private HealthAuthority bha;
	private Server bServer;

	private TrustedAuthority bta;
	private GroupManager bgm;
	private Proxy bProxy;
	private User bUser;
	private String type;


	@Before
	public void before() {

		nb_iter = 100;

		type = "a";
		System.out.println("Calculs faits avec le type " + type);

		bta = new TrustedAuthority(type);
		bServer = new Server(bta);
		bgm = new GroupManager(bta);
		bha = new HealthAuthority(bta, bgm.getDatabaseGM().getCrs());

		bProxy = new Proxy(bta, bgm);
		bUser = new User(bta, bha);
	}

	@Test
	public void setparamsTest() {
		long[] time = new long[nb_iter];

		TrustedAuthority ta;

		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			ta = new TrustedAuthority(type);
			long end = System.currentTimeMillis();
			time[i] = (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("setparams Test :");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void S_KeygenTest() {
		long[] time = new long[nb_iter];
		
		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			bta.s_keygen();
			long end = System.currentTimeMillis();
			time[i] = (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("S KeyGen Test :");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void HAKeyGen() {
		long[] time = new long[nb_iter];
		
		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			bta.ha_keygen();
			long end = System.currentTimeMillis();
			time[i] = (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("HA KeyGen Test :");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void SetupProxyTest() {
		GroupManager gm;
		long[] time = new long[nb_iter];
		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			gm = new GroupManager(bta);
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("Setup Proxy Test:");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void JoinProxyTest() {
		Proxy proxy;
		long[] time = new long[nb_iter];
		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			proxy = new Proxy(bta, bgm);
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);

		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("Join Proxy Test :");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void set_userIDTest() {
		long[] time = new long[nb_iter];
		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			bha.set_userIDha();
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);

		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("set_userID Test :");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void UserKeyGen() {
		User user;

		long[] time = new long[nb_iter];

		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			user = new User(bta, bha);
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("User KeyGen Test :");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");

	}

	@Test
	public void set_CCM_UTest() {

		long[] time = new long[nb_iter];

		for (int i = 0; i < nb_iter; i++) {
			long start = System.currentTimeMillis();
			bUser.set_CCM_U();
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("set_CCM_U Test:");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");

	}

	@Test
	public void S_PSign_STest() {

		long[] time = new long[nb_iter];

		for (int i = 0; i < nb_iter; i++) {
			long start = System.nanoTime();
			bServer.s_PSign_S(bta.getDatabase().getParam().getZr().newRandomElement());
			long end = System.nanoTime();
			time[i] = (long) (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("S_PSign_S Test:");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");

	}

	@Test
	public void P_Sign_Test() {
		long[] time = new long[nb_iter];

		for (int i = 0; i < nb_iter; i++) {
			Element e1 = bta.getDatabase().getParam().getZr().newRandomElement();
			Element e2 = bta.getDatabase().getParam().getG2().newRandomElement();
			long start = System.currentTimeMillis();
			bProxy.P_sign_P(e1, e2);
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("P_Sign Test:");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void SigVerify_Test() {
		long[] time = new long[nb_iter];
		boolean bool= false;;
		for (int i = 0; i < nb_iter; i++) {
			
			Element Ctc = bUser.set_CCM_U();
			Element[] ssig = bServer.s_PSign_S(Ctc);
			Contact psig = bProxy.P_sign_P(ssig[0], bUser.getIDu());
			bUser.add_contact(new ContactSigned(psig, ssig[1]));
			

			long start = System.currentTimeMillis();
			bool = bha.Sig_VerifyHA(bUser.getContact_list(), bgm.getDatabaseGM().getCrs());
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		assertTrue(bool);
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("Sig_Verify Test:");
		System.out.println(bool);
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void CCM_Verify_Test() {
		long[] time = new long[nb_iter];
		boolean bool = false;

		for (int i = 0; i < nb_iter; i++) {
			
			
			Element[] output = bServer.s_PSign_S(bUser.set_CCM_U());
			Element PS = output[0];
			Element PSp = output[1];
			Contact contact = bProxy.P_sign_P(PS, bUser.getIDu());
			Element M = (Element) contact.getM();
			bUser.add_contact(new ContactSigned(contact, PSp));
			/* -------------------*/

			
			long start = System.currentTimeMillis();
			bool  = bha.CCM_Verify(bServer, M, PSp, bUser.gettU());
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		System.out.println(bool);
		assertTrue(bool);

		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("CCM_Verify Test:");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}
	
	
	@Test
	public void SigVerify_Agg_Test() {
		long[] time = new long[nb_iter];
		boolean bool= false;;
		for (int i = 0; i < nb_iter; i++) {
			for (int j = 0; j < 100; j++) {
				Element Ctc = bUser.set_CCM_U();
				Element[] ssig = bServer.s_PSign_S(Ctc);
				Contact psig = bProxy.P_sign_P(ssig[0], bUser.getIDu());
				bUser.add_contact(new ContactSigned(psig, ssig[1]));
			}

			long start = System.currentTimeMillis();
			bool = bha.Sig_Verify_aggregation(bUser.getContact_list(), bgm.getDatabaseGM().getCrs());
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		assertTrue(bool);
		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("Sig_Verify_Agg Test:");
		System.out.println(bool);
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

	@Test
	public void CCM_Verify_Agg_Test() {
		long[] time = new long[nb_iter];
		boolean bool = false;

		for (int i = 0; i < nb_iter; i++) {
			
			for (int j = 0; j < 100; j++) {
				Element Ctc = bUser.set_CCM_U();
				Element[] ssig = bServer.s_PSign_S(Ctc);
				Contact psig = bProxy.P_sign_P(ssig[0], bUser.getIDu());
				bUser.add_contact(new ContactSigned(psig, ssig[1]));
			}


			
			long start = System.currentTimeMillis();
			bool  = bha.CCM_Verify_aggregation(bServer, bUser);
			long end = System.currentTimeMillis();
			time[i] = (long) (end - start);
		}
		System.out.println(bool);
		assertTrue(bool);

		double mean = Mean.mean(time);
		double sig = StandardDeviation.standardDev(time);
		System.out.println("CCM_Verify_Agg Test:");
		System.out.println("Number of iterations: " + nb_iter);
		System.out.println("Mean: " + mean + "\nStandard Deviation: " + sig + "\n");
	}

}

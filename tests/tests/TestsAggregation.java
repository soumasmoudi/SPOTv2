package tests;

import org.junit.Test;
import org.junit.BeforeClass;
import entities.GroupManager;
import entities.HealthAuthority;
import entities.Proxy;
import entities.Server;
import entities.TrustedAuthority;
import entities.User;
import it.unisa.dia.gas.jpbc.Element;
import statistics.base.Mean;
import utilities.Contact;
import utilities.ContactSigned;

public class TestsAggregation {
	private static String type = "a"; 
	private static TrustedAuthority ta;
	private static GroupManager gm;
	private static Proxy proxy;
	private static User user;
	private static Server server;
	private static HealthAuthority ha;

	private long start;
	private long end;
	//private long temps_normal;
	//private long temps_aggregation;

	private static int nb_iter = 100;
	private static int nb_contacts = 100;
	long[] time_Sig_VerifyHA = new long[nb_iter];
	long[] time_CCM_VerifyHA = new long[nb_iter];
	long[] time_Sig_VerifyAgg = new long[nb_iter];
	long[] time_CCM_VerifyAgg = new long[nb_iter];

	private static void generateContact(User user) {
		Element CCM = user.set_CCM_U();
		Element[] spsign = server.s_PSign_S(CCM);
		Element PS = spsign[0];
		Element PSp = spsign[1];
		Element ID = user.getIDu();
		Contact psign = proxy.P_sign_P(PS, ID);
		user.add_contact(new ContactSigned(psign,PSp));
		
	}
	
	@BeforeClass
	public static void generateContactList() {
		
		ta = new TrustedAuthority(type);
		gm = new GroupManager(ta);
		proxy = new Proxy(ta, gm);
		ha = new HealthAuthority(ta, gm.getDatabaseGM().getCrs());
		server = new Server(ta);
		
		user = new User(ta, ha);

		
		for (int i=0; i<nb_contacts;i++) {
			generateContact(user);// Contacts generation
		}
		
	}
	
	
	

	@Test
	public void testAggregation() { 
		System.out.println("Start test");
	
		for (int i = 0; i < nb_iter; i++) {
		start = System.nanoTime();//Without aggregation
		ha.Sig_VerifyHA(user.getContact_list(), gm.getDatabaseGM().getCrs());
		end = System.nanoTime();
		time_Sig_VerifyHA[i] = (long) (end - start);
		
		start = System.nanoTime();//With aggregation
		ha.Sig_Verify_aggregation(user.getContact_list(), gm.getDatabaseGM().getCrs());
		end = System.nanoTime();
		time_Sig_VerifyAgg[i] = end - start;
		
		start = System.nanoTime();//Without aggregation
		ha.CCM_Verify(server, user.getContact_list().get(0).getM(), user.getContact_list().get(1).getPSp(), user.gettU());
		end = System.nanoTime();
		time_CCM_VerifyHA[i] = (long) (end - start);
		
		
		start = System.nanoTime();//With aggregation
		ha.CCM_Verify_aggregation(server, user);
		end = System.nanoTime();
		time_CCM_VerifyAgg[i] = (long) (end - start);
		
		}
		

		System.out.println("Verification of group signature without aggregation: " + String.valueOf(Mean.mean(time_Sig_VerifyHA) / 1000000));
		System.out.println("Verification of group signature with aggregation: " + String.valueOf(Mean.mean(time_Sig_VerifyAgg) / 1000000));
		System.out.println("Verification of CCM without aggregation: " + String.valueOf(Mean.mean(time_CCM_VerifyHA) / 1000000));
		System.out.println("Verification of CCM with aggregation: " + String.valueOf(Mean.mean(time_CCM_VerifyAgg) / 1000000));
		System.out.println("Test performed on : " + java.util.Calendar.getInstance().getTime());
	}
	
}

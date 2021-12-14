package tests;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import entities.GroupManager;
import entities.HealthAuthority;
import entities.Proxy;
import entities.Server;
import entities.TrustedAuthority;
import entities.User;
import it.unisa.dia.gas.jpbc.Element;
import utilities.Contact;
import utilities.ContactSigned;

public class Aggregationvsnumber {
	
	private static String type = "a"; 
	private static TrustedAuthority ta;
	private static GroupManager gm;
	private static Proxy proxy;
	private static User user;
	private static Server server;
	private static HealthAuthority ha;

	private long start;
	private long end;
	

	private static int nb_iter = 14;
	private static int nb_contacts = 1000;
	List<Integer> contactnbr = new ArrayList<>();
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
			generateContact(user);// COntacts Generation
		}
		
	}
	
	@Test
	public void testAggregation() { 
		System.out.println("Start test");
		
		contactnbr.add(5);
		contactnbr.add(10); 
		contactnbr.add(20); 
		contactnbr.add(50);
		contactnbr.add(100); 
		contactnbr.add(200); 
		contactnbr.add(300);
		contactnbr.add(400);
		contactnbr.add(500); 
		contactnbr.add(600);
		contactnbr.add(700); 
		contactnbr.add(800); 
		contactnbr.add(900);
		contactnbr.add(1000);
		 
	
		for (int i = 0; i < contactnbr.size(); i++) {
	
		
		start = System.nanoTime();
		ha.Sig_Verify_aggregation(user.getContact_list().subList(0, contactnbr.get(i)), gm.getDatabaseGM().getCrs());
		end = System.nanoTime();
		time_Sig_VerifyAgg[i] = end - start;

		
		start = System.nanoTime();
		ha.CCM_Verify_aggregation(server, user);
		end = System.nanoTime();
		time_CCM_VerifyAgg[i] = (long) (end - start);
		
		}
		

		System.out.println("Verification of group signature with aggregation: " );
		for (int i = 0; i < time_Sig_VerifyAgg.length; i++ ) {
			System.out.println(String.valueOf(time_Sig_VerifyAgg[i] ));
		}
		System.out.println("Verification of CCM with aggregation: " );
		for (int i = 0; i < time_CCM_VerifyAgg.length; i++ ) {
			System.out.println(String.valueOf(time_CCM_VerifyAgg[i] ));
		}
		System.out.println("Test effectué le : " + java.util.Calendar.getInstance().getTime());
	}

}

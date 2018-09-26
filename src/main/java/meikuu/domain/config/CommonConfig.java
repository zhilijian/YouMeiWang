package meikuu.domain.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
	
	//佣金比例
	public static Double commissionRate = 0.7;
	
	//共享VIP
	public static Double shareVIPMonth = 29.0;
	public static Double shareVIPQuarter = 69.0;
	public static Double shareVIPYear = 99.0;
	//原创VIP
	public static Double originalVIPMonth = 25.0;
	public static Double originalVIPQuarter = 60.0;
	public static Double originalVIPYear = 210.0;
	//企业VIP
	public static Double companyVIPMonth = 69.0;
	public static Double companyVIPQuarter = 99.0;
	public static Double companyVIPYear = 199.0;
	
	
}

package bordomor.odtu.sk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Params 
{
	private static final String DB_HOST = "localhost";
	private static final String DB_NAME = "odtu_sk";
	
	public static final String DB_URL = "jdbc:postgresql://" + DB_HOST + "/" + DB_NAME + "?socketTimeout=10&loginTimeout=10&connectTimeout=10";
	public static final String DB_USERNAME = "camurkusu";
	public static final String DB_PWD = "alaska";
	
	public static final String SYSTEM_TITLE = "ODTÜ Spor Kulübü Yönetim Portalı";
	public static final String SYSTEM_VERSION = "0.01";
	public static final String SYSTEM_TITLE_STRING = SYSTEM_TITLE + " sür. " + SYSTEM_VERSION;
	
	public static final int MIN_TOKEN_LENGTH = 224;
	public static final int MAX_TOKEN_LENGTH = 256;
	public static final String SESSION_TOKEN_ABBREVIATION = "stok";
	public static final int MAX_SESSION_TOKEN_DURATION_SEC = 365*24*60*60;
	
	public static final Language DEFAULT_LANGUGAE = Language.TURKISH;
	public static final Locale DEFAULT_LOCALE = new Locale("tr", "TR");
	public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("Europe/Istanbul");
	
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	public static final DateTimeFormatter DAY_FORMATTER  = DateTimeFormatter.ofPattern("dd");
	
	public static SimpleDateFormat MONTH_NAME_FORMAT = new SimpleDateFormat("LLLL", DEFAULT_LOCALE);
	public static SimpleDateFormat DAY_MONTH_FORMAT = new SimpleDateFormat("dd MMMM", DEFAULT_LOCALE);
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", DEFAULT_LOCALE);
	public static SimpleDateFormat DATE_FORMAT_LONG = new SimpleDateFormat("dd MMM yyyy", DEFAULT_LOCALE);
	public static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", DEFAULT_LOCALE);
	
	public static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", DEFAULT_LOCALE);
	public static SimpleDateFormat DATE_TIME_FORMAT_LONG = new SimpleDateFormat("dd MMM yyyy HH:mm", DEFAULT_LOCALE);
	public static SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", DEFAULT_LOCALE);
	public static SimpleDateFormat ISO8601_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", DEFAULT_LOCALE);
	public static String[] MONTH_TR_VALS = new String[] {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
	public static String[] MONTH_VALS = new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
	
	//Servlet hata dönüşleri ve standart tanımlamalar
	public static final String CODE_ERROR_STRING = "hata:01";
	public static final String ILLEGAL_SERVLET_PARAMETER_ERROR_STRING = "hata:02";
	public static final String SQL_EXCEPTION_ERROR_STRING = "hata:03";
	public static final String NO_DATA_ERROR_STRING = "hata:04";
	public static final String INVALID_TOKEN_ERROR_STRING = "hata:05";
	public static final String UNAUTHORIZED_USER_ERROR_STRING = "hata:06";
	public static final String DATABASE_CONNECTION_ERROR_STRING = "hata:07";
	public static final String EMAIL_SENDING_ERROR_STRING = "hata:08";
	public static final String QUOTA_OVERFLOW_ERROR_STRING = "hata:09";
	public static final String TOKEN_IDENTIFIER_STRING = SESSION_TOKEN_ABBREVIATION + ":";
	public static final String SYSTEM_USER_IDENTIFIER_STRING = "kullanici tipi:sistem";
	public static final String CLIENT_USER_IDENTIFIER_STRING = "kullanici tipi:musteri";
	public static final String DATA_MANIPULATION_RESULT_STRING = "sonuc:1";
	public static final String DATA_MANIPULATION_RESULT_TOKEN = "sonuc:";
	
	public static final String DATABASE_DELETE_DATA_MODE = "0";
	public static final String DATABASE_INSERT_DATA_MODE = "1";
	public static final String DATABASE_UPDATE_DATA_MODE = "2";
	
	//Veritabanı hata kodları
	public static String DATABASE_CONNECTION_ERROR_CODE = "08001";
	public static String DATABASE_UNIQUE_CONSTRAINT_ERROR_CODE = "23505";
	
	//Enum'lar
	public static enum PaymentSchemaType { INTERVAL_CONSTRAINED, EQUIDISTRIBUTED };
	public static enum PaymentFailureAction { NO_ACTION, REMOVE_FROM_ACTIVITIES, SUSPEND, DELETE };
	public static enum SubscriptionCancellationAction { ENFORCE_PLAN, PACIFY_FUTURE_PAYMENTS, CANCEL_FUTURE_PAYMENTS };
	
	public static enum DeviceType{ BROWSER, MOBILE };
	
	public static enum BloodType
	{ 
		A_RH_POS("A Rh Pozitif"), B_RH_POS("B Rh Pozitif"), ZR_RH_POS("0 Rh Pozitif"), AB_RH_POS("AB Rh Pozitif"), 
		A_RH_NEG("A Rh Negatif"), B_RH_NEG("B Rh Negatif"), ZR_RH_NEG("0 Rh Negatif"), AB_RH_NEG("AB Rh Negatif");
		
		private final String name;
		
		private BloodType(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	};
	
	public static enum LoginableState
	{ 
		PENDING("Beklemede"), PENDING_CONFIRMATION("Onay Bekliyor"), ACTIVE("Etkin"), SUSPENDED("Askıda");
		
		private final String desc;
		
		private LoginableState(String desc)
		{
			this.desc = desc;
		}
		
		public String getDesc()
		{
			return this.desc;
		}
	};
	
	public static enum MembershipState
	{ 
		STANDARD("Standart"), FROZEN("Kayıt Dondurulmuş"), DISMISSED("İlişiği Kesilmiş");
		
		private final String desc;
		
		private MembershipState(String desc)
		{
			this.desc = desc;
		}
		
		public String getDesc()
		{
			return this.desc;
		}
	};
	
	
	public static enum ManipulationMode
	{ 
		FF("Yetki Yok"), FT("Yaratma ve Düzenleme"), TF ("Yalnızca Görüntüleme"), TT("Görüntüleme, Yaratma ve Düzenleme");
		
		private final String desc;
		
		private ManipulationMode(String desc)
		{
			this.desc = desc;
		}
		
		public String getDesc()
		{
			return this.desc;
		}
		
		public boolean canRead()
		{
			return this.toString().charAt(0) == 'T';
		}
		
		public boolean canWrite()
		{
			return this.toString().charAt(1) == 'T';
		}
		
		public boolean isUnauthorized()
		{
			return this == ManipulationMode.FF;
		}
	};
	
	public static enum Gender
	{ 
		MALE("Erkek"), FEMALE("Kız"), PREFER_NOT_TO_SAY("Belirtmek İstemiyor");
		
		private final String name;
		
		private Gender(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	};
	
	public static enum AgeGroup
	{
		U21("U-21", "21 Yaş Grubu"), U20("U-20", "20 Yaş Grubu"), U19("U-19", "19 Yaş Grubu"), U18("U-18", "18 Yaş Grubu"), U17("U-17", "17 Yaş Grubu"), U16("U-16", "16 Yaş Grubu"), U15("U-15", "15 Yaş Grubu"), U14("U-14", "14 Yaş Grubu"), U13("U-13", "13 Yaş Grubu"), U12("U-12", "12 Yaş Grubu"), U11("U-11", "11 Yaş Grubu"), U10("U-10", "10 Yaş Altı");
		
		private final String name;
		private final String longName;
		
		private AgeGroup(String name, String longName)
		{
			this.name = name;
			this.longName = longName;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getLongName()
		{
			return this.longName;
		}
		
		public int getValue()
		{
			return Integer.parseInt(this.toString().substring(1));
		}
		
		public static AgeGroup getGroup(String birthDate) throws ParseException
		{
			Date today = new Date();
			Date bDate = new SimpleDateFormat("dd/MM/yyyy").parse(birthDate);
			
			long timeDiff = today.getTime() - bDate.getTime();
			double yearDiff = timeDiff/(1000d*3600*24*365);
			
			if(yearDiff >= 18d  && yearDiff <= 20)
				return AgeGroup.U20;
			else if(yearDiff >= 16d)
				return AgeGroup.U18;
			else if(yearDiff >= 14d)
				return AgeGroup.U16;
			else if(yearDiff >= 12d)
				return AgeGroup.U14;
			else if(yearDiff >= 10d)
				return AgeGroup.U12;
			else if(yearDiff >= 0d)
				return AgeGroup.U10;
			
			throw new IllegalArgumentException("Unable to calculate age group.");
		}
	};
	
	public static enum TrainerLabel
	{ 
		MASTER("Usta Antrenör"), SENIOR("Kıdemli Antrenör"), PROFICIENT("Yetkin Antrenör"), BEGINNER("Kıdemsiz Antrenör"), TRAINEE("Çömez Antrenör"); 
		
		private final String name;
		
		private TrainerLabel(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	};
	
	public static enum EventType 
	{ 
		OFFICIAL_MATCH("Resmi Müsabaka"), FRIENDLY_MATCH("Özel Müsabaka"), TOURNAMENT("Turnuva"), CAMP("Kamp"), OTHER("Diğer");
		
		private final String name;
		
		private EventType(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	};
	
	public static enum MemberRole 
	{ 
		GROUP_HEAD("Kafile Başkanı"), HEAD_TRAINER("Baş Antrenör"), TRAINER("Antrenör"), ATHLETE("Sporcu");
		
		private final String name;
		
		private MemberRole(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	};
	
	public static enum LocationType 
	{ 
		FACILITY("Antrenman Tesisi"), EVENT_POINT("Etkinlik Noktası"), PRIVATE_ADDRESS("Özel Adres"), ACCOMODATION_LOCATION("Konaklama Tesisi"), MEETING_POINT("Buluşma Noktası"), OTHER("Diğer");
		
		private final String name;
		
		private LocationType(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	};
	
	public static enum Currency
	{
		TRY("Türk Lirası", "₺"), USD("Amerikan Doları", "$"), EUR("Avro", "€"), GBP("İngiliz Sterlini", "£"), RUB("Rus Rublesi", "₽");
		
		private final String currencyName;
		private final String symbol;
		
		private Currency(String currencyName, String symbol)
		{
			this.currencyName = currencyName;
			this.symbol = symbol;
		}
		
		public String getCurrencyName()
		{
			return this.currencyName;
		}
		
		public String getSymbol()
		{
			return this.symbol;
		}
	};
	
	public static enum Language
	{
		TURKISH("TR", "Türkçe"), ENGLISH("EN", "English"), GERMAN("DE", "Deutsch"), OTHER("OTHER", "Other");
		
		private final String abbreviation;
		private final String localName;
		
		private Language(String abbreviation, String localName)
		{
			this.abbreviation = abbreviation;
			this.localName = localName;
		}
		
		public String getAbbreviation()
		{
			return this.abbreviation;
		}
		
		public String getLocalName()
		{
			return this.localName;
		}
		
		public static Language build(String abbreviation)
		{
			if(abbreviation.toUpperCase().equals(TURKISH.getAbbreviation()))
				return TURKISH;
			else if(abbreviation.toUpperCase().equals(ENGLISH.getAbbreviation()))
				return ENGLISH;
			else if(abbreviation.toUpperCase().equals(GERMAN.getAbbreviation()))
				return GERMAN;
			else if(abbreviation.toUpperCase().equals(OTHER.getAbbreviation()))
				return OTHER;
			else
				return TURKISH;
		}
	};
	
	public static enum TimeUnit
	{
		DAYS("Gün", "Günlük"), WEEKS("Hafta", "Haftalık"), MONTHS("Ay", "Aylık"), YEARS("Yıl", "Yıllık");
		
		private final String name;
		private final String periodName;
		
		private TimeUnit(String name, String periodName)
		{
			this.name = name;
			this.periodName = periodName;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getPeriodName()
		{
			return this.periodName;
		}
		
		public ChronoUnit getChronoUnit()
		{
			if(this == TimeUnit.DAYS)
				return ChronoUnit.DAYS;
			else if(this == TimeUnit.WEEKS)
				return ChronoUnit.WEEKS;
			else if(this == TimeUnit.MONTHS)
				return ChronoUnit.MONTHS;
			else if(this == TimeUnit.YEARS)
				return ChronoUnit.YEARS;
			else
				return null;
		}
	};
	
	public static enum RegistrationType
	{
		TRIAL("Deneme Kaydı"), CERTAIN("Kesin Kayıt");
		
		private final String name;
		
		private RegistrationType(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	}
	
	public static enum RegistrationState
	{
		INITIALIZED("Yaratıldı"), IN_PROGRESS("Devam Ediyor"), PENDING("Beklemede"), COMPLETED("Tamamlandı");
		
		private final String name;
		
		private RegistrationState(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	}
	
	public static enum RegistrationStep
	{
		ATHLETE_DATA("Sporcu Bilgileri"), MEDICAL_DATA("Sağlık Bilgileri"), PARENT_DATA("Veli Bilgileri"), TRAINING_SELECTION("Antrenma Seçimi"), PAYMENT_PLAN("Ödeme Planı Seçimi"), PAYMENT("Ödeme");
		
		private final String name;
		
		
		
		private RegistrationStep(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return this.name;
		}
	}
	
	public static enum ParenthoodTitle
	{
		MOTHER("Anne", Gender.FEMALE), FATHER("Baba", Gender.MALE), SISTER("Abla", Gender.FEMALE), BROTHER("Ağabey", Gender.MALE), 
		GRANDMOTHER("Büyükanne", Gender.FEMALE), GRANDFATHER("Büyükbaba", Gender.MALE), OTHER("Diğer", Gender.PREFER_NOT_TO_SAY);
		
		private final String title;
		private final Gender parenthoodGender;
		
		private ParenthoodTitle(String title, Gender parenthoodGender)
		{
			this.title = title;
			this.parenthoodGender = parenthoodGender;
		}
		
		public String getTitle()
		{
			return this.title;
		}
		
		public Gender getParenthoodGender()
		{
			return this.parenthoodGender;
		}
	}
	
	public static final Currency DEFAULT_CURRENCY = Currency.TRY;
	
	public static enum PaymentState
	{
		PENDING("Bekliyor"), NO_PAYMENT("Ödenmedi"), PARTIAL_PAYMENT("Kısmi Ödeme"), PAID("Ödendi"), LATE_PAYMENT("Geç Ödeme");
		
		private final String desc;
		
		private PaymentState(String desc)
		{
			this.desc = desc;
		}
		
		public String getDescription()
		{
			return desc;
		}
	}
	
	//Klasör ve Kaynak Hedefleri
	public static final String TMP_S3_STORAGE_PATH = "/tmp/odtu_sk/s3/";
	public static final String DATA_STORAGE_PATH = "/vol/pgsql_vol/odtu_sk/local/data/";
	public static final String THUMBNAIL_STORAGE_PATH = "/vol/pgsql_vol/odtu_sk/local/thumbnail/";
	public static final String FORMS_FOLDER_URL = DATA_STORAGE_PATH + "form/";
	
	//IP & URLLER
	public static final String PORTAL_INDEX_URI = "/index.jsp";
	public static final String PORTAL_REGISTER_INDEX_URI = "/registration/index.jsp";
	public static final String PORTAL_TRIAL_REGISTER_URI = "/registration/trial/index.jsp";
	public static final String PORTAL_CERTAIN_REGISTER_URI = "/registration/certain/index.jsp";
	public static final String PORTAL_PAYMENT_URI = "/registration/payment.jsp";
	public static final String PORTAL_3DS_SUCCESS_URI = "/result/3ds_success.jsp";
	public static final String PORTAL_3DS_FAILURE_URI = "/result/3ds_failure.jsp";
	
	public static final String PORTAL_BRANCH_SETTINGS_URI = "/settings/branch_settings.jsp";
	public static final String LOGOUT_URI = "/auth/logout.jsp";
	
	public static final String PORTAL_CLUB_MANAGER_HOME_URI = "/home.jsp";
	public static final String PORTAL_BRANCH_MANAGER_HOME_URI = "/branch_manager/home.jsp";
	public static final String PORTAL_TRAINER_HOME_URI = "/trainer/home.jsp";
	public static final String PORTAL_PARENT_HOME_URI = "/parent/home.jsp";
	public static final String PORTAL_ATHLETE_HOME_URI = "/athlete/home.jsp";
	public static final String PORTAL_DONOR_HOME_URI = "/donor/home.jsp";
	public static final String PORTAL_EMPLOYEE_HOME_URI = "/employee/home.jsp";
	
	public static final String PORTAL_LOGIN_ROLES_URI = "/login_roles.jsp";
	public static final String PORTAL_USER_ACCOUNTS_URI = "/user_accounts.jsp";
	public static final String PORTAL_ATHLETES_URI = "/athletes.jsp";
	public static final String PORTAL_TEAMS_URI = "/teams.jsp";
	public static final String PORTAL_TRAINERS_URI = "/trainers.jsp";
	public static final String PORTAL_LOCATIONS_URI = "/locations.jsp";
	public static final String PORTAL_PAYMENT_SCHEMAS_URI = "/payment_schemas.jsp";
	public static final String PORTAL_PROMOTIONS_URI = "/promotions.jsp";
	public static final String PORTAL_TRAININGS_URI = "/trainings.jsp";
	public static final String PORTAL_EVENTS_URI = "/events.jsp";
	
	public static final String PORTAL_ACCOUNT_RECOVERY_URI = "/account/recover.jsp";
	public static final String PORTAL_PWD_RESET_URI = "/account/reset_pwd.jsp";
	public static final String PORTAL_PROC_RESULT_URI = "/account/proc_result.jsp";
	
	//e-posta parametreleri
	public static final String EMAILER_ACCOUNT = "test@test.com";
	public static final String EMAILER_ACCOUNT_PWD = "pwd";
	public static final String EMAILER_ACCOUNT_SMTP_SERVER_URL = "mx-out04.natrohost.com";
	public static final int EMAILER_ACCOUNT_SSL_PORT = 465;
	public static final String[] CONTACT_FORM_RECIPIENTS = new String[] {"kagan.gedikli@msd.com"};
	
	public static final String NAME_REGEX = "^([A-Za-zÇĞİÖŞÜçğıöşü][A-Za-zÇĞİÖŞÜçğıöşü]*[.]+?\\s)*(([A-Za-zÇĞİÖŞÜçğıöşü]){2,}\\s?)+$";
	public static final String SURNAME_REGEX = "^([A-Za-zÇĞİÖŞÜçğıöşü]{2,}\\s?)+$";
	public static final String EMAIL_REGEX = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.([a-z]{2,})+$";
	public static final String PWD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d).{8,16}$";
	public static final String PHONE_REGEX = "^([+]|(00))?([0]|(90))\\s?([0-9]{3}|[(][0-9]{3}[)])\\s?[0-9]{3}\\s?[0-9]{2}\\s?[0-9]{2}$"; 
	public static final String BIRTH_DATE_REGEX = "^[0-3][0-9][\\/\\-.][0-1][0-9][\\/\\-.][1-2][0-9][0-9][0-9]$";
	public static final int MIN_MESSAGE_LENGTH = 5;
}

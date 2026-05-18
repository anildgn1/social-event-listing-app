package com.example.eventlisting.database;

import com.example.eventlisting.Event;
import com.example.eventlisting.R;

public class SampleEventData {

    public static void insertEvents(EventDao eventDao) {
        eventDao.insert(new Event(
                "Grand Symphony Night",
                "2024-12-15",
                "Müzik tutkunları için unutulmaz bir geceye hazır olun! İstanbul Filarmoni Orkestrası, klasik müzikten modern eserler kadar geniş bir repertuarla sizi büyüleyecek.",
                50, 41.0082, 28.9784, "İstanbul", "Beşiktaş", "Konser", true, R.drawable.orchestra, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Visionary Art Exhibition",
                "2024-12-20",
                "Sanatseverleri çağdaş sanatın en dikkat çekici eserleriyle buluşturuyoruz! Kadıköy’deki Barış Manço Kültür Merkezi’nde düzenlenecek sergide, çarpıcı eserler sizleri bekliyor.",
                100, 40.9901, 29.0282, "İstanbul", "Kadıköy", "Sanat", false, R.drawable.art_exhibition, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Tech Horizons Meetup",
                "2024-12-18",
                "Teknoloji meraklılarını bir araya getiren bu etkinlikte, sektörün önde gelen isimleriyle tanışma ve geleceğin teknolojilerini keşfetme fırsatı bulacaksınız.",
                30, 39.9208, 32.8541, "Ankara", "Çankaya", "Teknoloji", true, R.drawable.tech_meetup, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Belgrad Ormanı Yürüyüş Etkinliği",
                "2024-09-15",
                "Doğa severleri bir araya getiren rehber eşliğindeki bu yürüyüş etkinliği, keyifli bir piknikle tamamlanacak.",
                75, 41.1865, 28.9786, "İstanbul", "Sarıyer", "Spor", true, R.drawable.belgrad_hiking_event, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Bostancı Sahil Bisiklet Turu",
                "2024-10-07",
                "Sağlıklı yaşamı teşvik eden bu bisiklet turu, sahil boyunca eşsiz bir parkur sunuyor.",
                100, 40.9526, 29.0941, "İstanbul", "Kadıköy", "Spor", true, R.drawable.bostanci_cycling_tour, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Florya Plaj Voleybolu Turnuvası",
                "2024-08-30",
                "Florya sahilinde düzenlenecek olan bu turnuva, hem amatör hem profesyonel takımları ağırlayacak.",
                200, 40.9712, 28.8175, "İstanbul", "Bakırköy", "Spor", false, R.drawable.florya_beach_volleyball, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Şişli Kişisel Gelişim Semineri",
                "2024-11-01",
                "Zaman yönetimi ve motivasyon temalı bu seminer, katılımcılara önemli yaşam becerileri kazandıracak.",
                120, 41.0605, 28.9875, "İstanbul", "Şişli", "Eğitim", true, R.drawable.sisli_personal_development, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Fatih Tarihi Eserler Fotoğrafçılığı Atölyesi",
                "2024-10-18",
                "Fatih’in tarihi dokusunu fotoğraflamak isteyenler için düzenlenen bu etkinlikte profesyonel rehberler eşlik edecek.",
                50, 41.0085, 28.9802, "İstanbul", "Fatih", "Eğitim", false, R.drawable.fatih_photography_workshop, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Beylikdüzü Robotik Kodlama Eğitimi",
                "2024-09-22",
                "Çocuklara yönelik robotik kodlama eğitimi ile STEM becerileri geliştiriliyor.",
                40, 41.0012, 28.6416, "İstanbul", "Beylikdüzü", "Eğitim", true, R.drawable.beylikduzu_robotics_coding, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Eminönü Sokak Lezzetleri Festivali",
                "2024-11-15",
                "İstanbul’un eşsiz sokak lezzetleri Eminönü’nde ziyaretçilerle buluşuyor.",
                300, 41.0165, 28.9742, "İstanbul", "Fatih", "Gastronomi", false, R.drawable.eminonu_food_festival, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Beşiktaş Çikolata ve Kahve Günleri",
                "2024-12-03",
                "Çikolata ve kahve tutkunlarını bir araya getiren bu etkinlikte özel tadımlar ve workshoplar düzenlenecek.",
                200, 41.0422, 29.0042, "İstanbul", "Beşiktaş", "Gastronomi", true, R.drawable.besiktas_chocolate_coffee, null, "test@example.com"
        ));

        eventDao.insert(new Event(
                "Kadıköy Vegan Yemek Festivali",
                "2024-10-10",
                "Moda Sahili’nde düzenlenecek olan bu festival, sağlıklı vegan yiyeceklerin tanıtılmasını amaçlıyor.",
                150, 40.9840, 29.0255, "İstanbul", "Kadıköy", "Gastronomi", false, R.drawable.kadikoy_vegan_food, null, "test@example.com"
        ));
    }
}

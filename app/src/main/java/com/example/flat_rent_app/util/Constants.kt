object Constants {
    const val PHOTO_BASE_URL = "http://92.63.96.49:8000/"

    const val SMOKING_ALLOWED = "Курение разрешено"
    const val DRINKS_ALCOHOL = "Пью алкоголь"
    const val NIGHT_OWL = "Сова"
    const val EARLY_BIRD = "Жаворонок"
    const val HAS_PETS = "Есть животные"
    const val INVITES_GUESTS = "Приглашаю гостей"
    const val VALUES_CLEANLINESS = "Чистота важна"
    const val VALUES_QUIET = "Тишина важна"
    const val LOVES_MUSIC = "Люблю музыку"
    const val DOES_SPORTS = "Занимаюсь спортом"

    const val UNIVERSITY_ALL = "Все ВУЗы"
    const val UNIVERSITY_BAUMAN = "МГТУ им. Н.Э. Баумана"
    const val UNIVERSITY_HSE = "НИУ ВШЭ"
    const val UNIVERSITY_MSU = "МГУ имени М. В. Ломоносова"

    val UNIVERSITIES_LIST = listOf(
        UNIVERSITY_ALL,
        UNIVERSITY_BAUMAN,
        UNIVERSITY_HSE,
        UNIVERSITY_MSU
    )

    val UNIVERSITIES_FOR_PROFILE = listOf(
        UNIVERSITY_BAUMAN,
        UNIVERSITY_HSE,
        UNIVERSITY_MSU
    )

    val AGES_FOR_PROFILE = (16..35).map { it.toString() }

    val CITIES_FOR_PROFILE = listOf(
        "Москва"
    )
    const val GENDER_MALE = "Мужской"
    const val GENDER_FEMALE = "Женский"
    const val GENDER_ANY = "Любой"

    val GENDERS_LIST: List<String> = listOf(
        GENDER_ANY,
        GENDER_MALE,
        GENDER_FEMALE
    )

    const val AGE_MIN_DEFAULT = 18
    const val AGE_MAX_DEFAULT = 40
}
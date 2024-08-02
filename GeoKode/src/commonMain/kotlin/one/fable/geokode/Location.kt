package one.fable.geokode

data class Location (


    /*
    The altitude of this location in meters above the WGS84 reference ellipsoid.
    Android: https://developer.android.com/reference/android/location/Location#getAltitude()
    The altitude as a height above the World Geodetic System 1984 (WGS84) ellipsoid, measured in meters.
    iOS: https://developer.apple.com/documentation/corelocation/cllocation/altitude
     */
//    val ellipsoidalAltitude: Double? = null,
//    val horizontalAccuracy: Float? = null,
//    val verticalAccuracy: Float? = null,
//
//    val timestamp: Long? = null,
//    val provider: String? = null,
//    val speed : Float? = null,
//    val distance : Float? = null,

    val featureName: String? = null,
    val addressLines: List<String>? = null,
    val maxAddressLineIndex: Int? = -1,
    val adminArea: String? = null,
    val subAdminArea: String? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val thoroughfare: String? = null,
    val subThoroughfare: String? = null,
    val premises: String? = null,
    val postalCode: String? = null,
    val countryCode: String? = null,
    val countryName: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasLatitude: Boolean = false,
    val hasLongitude: Boolean = false,
    val phone: String? = null,
    val url: String? = null,
    val extras: List<Pair<String, Any?>> = emptyList(),

//    private String mFeatureName;
//    private HashMap<Integer, String> mAddressLines;
//    private int mMaxAddressLineIndex = -1;
//    private String mAdminArea;
//    private String mSubAdminArea;
//    private String mLocality;
//    private String mSubLocality;
//    private String mThoroughfare;
//    private String mSubThoroughfare;
//    private String mPremises;
//    private String mPostalCode;
//    private String mCountryCode;
//    private String mCountryName;
//    private double mLatitude;
//    private double mLongitude;
//    private boolean mHasLatitude = false;
//    private boolean mHasLongitude = false;
//    private String mPhone;
//    private String mUrl;
//    private Bundle mExtras = null;
)
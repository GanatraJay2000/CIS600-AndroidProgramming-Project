
import com.example.project.models.PlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {
    @GET("place/textsearch/json")
    fun searchPlaces(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("key") apiKey: String
    ):Response<PlacesResponse>
}

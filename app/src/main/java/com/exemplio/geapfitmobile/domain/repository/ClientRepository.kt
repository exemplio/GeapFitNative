import com.exemplio.geapfitmobile.domain.entity.UserEntity

interface ClientRepository {
    suspend fun getClients():List<UserEntity>
}
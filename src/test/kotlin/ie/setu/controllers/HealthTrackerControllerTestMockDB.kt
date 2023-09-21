package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.validEmail
import ie.setu.helpers.validName
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.Unirest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthTrackerControllerTestMockDB {

    //Don't connect to the production database; we will try mock this.
    // private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Test
    fun `getting a user by id when id exists, returns a 200 response`() {

        // Arrange - create a mock user
        val testUser = User(1234, validName, validEmail)

        // Arrange - create a mock database and detail expected behaviour
        val mockUserDatabase = Mockito.mock(UserDAO::class.java)
        `when`(mockUserDatabase.save(testUser)).thenReturn(1234)
        `when`(mockUserDatabase.findById(1234)).thenReturn(testUser)

        // Act - add the user and retrieve it
        val addedUser: Int? = mockUserDatabase.save(testUser)
        val retrieveResponse = addedUser?.let { retrieveUserById(it) }

        // Assert - verify return code
        Assertions.assertEquals(200, retrieveResponse!!.status)
        val retrievedUser : User = jsonToObject(retrieveResponse.body.toString())
        Assertions.assertEquals(validEmail, retrievedUser.email)
        Assertions.assertEquals(validName, retrievedUser.name)

        // After - no need to delete the user, as we are using a mock database
    }

    //helper function to retrieve a test user from the database by id
    private fun retrieveUserById(id: Int): HttpResponse<String> {
        return Unirest.get(origin + "/api/users/${id}").asString()
    }

}
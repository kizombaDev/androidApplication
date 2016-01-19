/* This file contains the UsersController class.
* This controller is responsible for all resources which are located under the path /users
*
* Datei: UsersController.cs Autor: Ramandeep Singh
* Datum: 23.12.2015 Version: 1.0
*
* Historie:
* 19.01.2016 Ramandeep Singh:   Added phone number normalization
                                If user to create already exists the existing user will be updated.
*/

using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using Webserver.Models;
using Webserver.Utils;

namespace Webserver.Controllers
{
    /// <summary>
    ///     This class defines methodes for resources which belong to /users
    /// </summary>
    public class UsersController : ApiController
    {
        private readonly DatabaseContext databaseContext = new DatabaseContext();

        /// <summary>
        ///     Get all existing users with all properties.
        /// </summary>
        /// <returns>Returns an array of alle users.</returns>
        [ResponseType(typeof (User[]))]
        public async Task<IHttpActionResult> Get()
        {
            var users = await databaseContext.Users.ToListAsync();
            return Ok(users);
        }

        /// <summary>
        ///     Creates a new user
        /// </summary>
        /// <param name="user">
        ///     The user to create. The user should have at least a phone number and name.
        /// </param>
        /// <returns>
        ///     Returns the crated user with all written properties and the generated ID if successful.
        ///     If the user model is invalid a HTTP statuscode 400 will be returned.
        /// </returns>
        [ResponseType(typeof (User))]
        public async Task<IHttpActionResult> PostUser(User user)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            user.Normalize();
            var existingUser = await databaseContext.Users.FirstOrDefaultAsync(x => x.PhoneNumber == user.PhoneNumber);
            if (existingUser != null)
            {
                existingUser.Username = user.Username;
                user = existingUser;
            }
            else
            {
                databaseContext.Users.Add(user);
            }

            await databaseContext.SaveChangesAsync();

            return Ok(user);
        }

        /// <summary>
        ///     Deletes an existing user.
        /// </summary>
        /// <param name="id">The id of the user to delete.</param>
        /// <returns>
        ///     Returns the deleted user if successful. If no user exists with the given ID a HTTP statuscode 404 will be returned.
        /// </returns>
        [ResponseType(typeof (User))]
        public async Task<IHttpActionResult> DeleteUser(int id)
        {
            var user = await databaseContext.Users.FindAsync(id);
            if (user == null)
            {
                return NotFound();
            }

            databaseContext.Users.Remove(user);
            await databaseContext.SaveChangesAsync();

            return Ok(user);
        }

        /// <summary>
        ///     Disposes the controller instance.
        /// </summary>
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                databaseContext.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool UserExists(int id)
        {
            return databaseContext.Users.Count(e => e.Id == id) > 0;
        }
    }
}
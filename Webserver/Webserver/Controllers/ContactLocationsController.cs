/* This file contains the LocationsController class.
* This controller is responsible for all resources which are located under the path /contact-locations
*
* Datei: ContactLocationsController.cs Autor: Ramandeep Singh
* Datum: 23.12.2015 Version: 1.0
*
* Historie:
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
    ///     This class defines methodes for resources which belong to /contact-locations
    /// </summary>
    public class ContactLocationsController : ApiController
    {
        private readonly DatabaseContext databaseContext = new DatabaseContext();

        /// <summary>
        ///     This method provides the location data of all specified users.
        /// </summary>
        /// <param name="users">
        ///     The users whose locations should be read. The user instances only should contain the phone number.
        /// </param>
        /// <returns>
        ///     Returns a list of all users which were defined and could be found in the database.
        /// </returns>
        [ResponseType(typeof (User[]))]
        public async Task<IHttpActionResult> Post(User[] users)
        {
            foreach (var user in users)
            {
                user.Normalize();
            }

            var phoneNumbers = users.Select(x => x.PhoneNumber).ToArray();
            var matchingUsers =
                await databaseContext.Users.Where(x => phoneNumbers.Contains(x.PhoneNumber)).ToListAsync();

            return Ok(matchingUsers);
        }

        /// <summary>
        ///     This method provides the location data of the specified user.
        /// </summary>
        /// <param name="id">The id of the user whose location should be read.</param>
        /// <returns>
        ///     Returns the use which was specified by the ID. If the user does not exists a HTTP statuscode 404 will be
        ///     returned.
        /// </returns>
        [ResponseType(typeof (User))]
        public async Task<IHttpActionResult> Post(int id)
        {
            var matchingUser = await databaseContext.Users.FindAsync(id);
            if (matchingUser == null)
            {
                return NotFound();
            }

            return Ok(matchingUser);
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
    }
}
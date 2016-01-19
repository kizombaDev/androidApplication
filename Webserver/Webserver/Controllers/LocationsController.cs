/* This file contains the LocationsController class.
* This controller is responsible for all resources which are located under the path /locations
*
* Datei: LocationsController.cs Autor: Ramandeep Singh
* Datum: 23.12.2015 Version: 1.0
*
* Historie:
* 19.01.2016 Ramandeep Singh: Added phone number normalization
*/

using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using Webserver.Models;
using Webserver.Utils;

namespace Webserver.Controllers
{
    /// <summary>
    ///     This class defines methodes for resources which belong to /locations
    /// </summary>
    public class LocationsController : ApiController
    {
        private readonly DatabaseContext databaseContext = new DatabaseContext();

        /// <summary>
        ///     This method updates the location data of an existing user.
        /// </summary>
        /// <param name="id">The ID of the user to update.</param>
        /// <param name="user">The user instance which contains the data to set.</param>
        /// <returns>
        ///     If the input data is invalid a HTTP statuscode 400 is returned.
        ///     If no user with the given ID exists a HTTP statuscode 404 is returned.
        ///     If update was successful HTTP statuscode 200 is returned.
        /// </returns>
        [ResponseType(typeof (void))]
        public async Task<IHttpActionResult> Put(int id, User user)
        {
            var isValid = ValidateForUpdateLocation(user);
            if (!isValid)
            {
                return BadRequest(ModelState);
            }

            if (id != user.Id)
            {
                return BadRequest();
            }

            var originalUser = await databaseContext.Users.FindAsync(id);
            if (originalUser == null)
            {
                return NotFound();
            }

            user.Normalize();
            originalUser.Longitude = user.Longitude;
            originalUser.Latitude = user.Latitude;
            originalUser.LocationUpdateTime = user.LocationUpdateTime;

            try
            {
                await databaseContext.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!UserExists(id))
                {
                    return NotFound();
                }
                throw;
            }

            return Ok();
        }

        private bool ValidateForUpdateLocation(User user)
        {
            var isValid = true;

            if (user.LocationUpdateTime == null)
            {
                ModelState.AddModelError("LocationUpdateTime", "Das Feld LocationUpdateTime ist erforderlich.");
                isValid = false;
            }
            if (user.Longitude == null)
            {
                ModelState.AddModelError("Longitude", "Das Feld Longitude ist erforderlich");
                isValid = false;
            }
            if (user.Latitude == null)
            {
                ModelState.AddModelError("Latitude", "Das Feld Latitude ist erforderlich");
                isValid = false;
            }

            return isValid;
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
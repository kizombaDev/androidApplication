using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using Webserver.Models;

namespace Webserver.Controllers
{
    public class LocationsController : ApiController
    {
        private readonly DatabaseContext databaseContext = new DatabaseContext();

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
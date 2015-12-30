using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using Webserver.Models;

namespace Webserver.Controllers
{
    public class ContactLocationsController : ApiController
    {
        private readonly DatabaseContext databaseContext = new DatabaseContext();

        [ResponseType(typeof (User[]))]
        public async Task<IHttpActionResult> Post(User[] users)
        {
            var phoneNumbers = users.Select(x => x.PhoneNumber).ToArray();
            var matchingUsers =
                await databaseContext.Users.Where(x => phoneNumbers.Contains(x.PhoneNumber)).ToListAsync();

            return Ok(matchingUsers);
        }

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
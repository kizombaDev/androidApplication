using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using Webserver.Models;

namespace Webserver.Controllers
{
    public class UsersController : ApiController
    {
        private readonly DatabaseContext databaseContext = new DatabaseContext();

        [ResponseType(typeof (User[]))]
        public async Task<IHttpActionResult> Get()
        {
            var users = await databaseContext.Users.ToListAsync();
            return Ok(users);
        }

        [ResponseType(typeof (User))]
        public async Task<IHttpActionResult> PostUser(User user)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            databaseContext.Users.Add(user);

            try
            {
                await databaseContext.SaveChangesAsync();
            }
            catch (DbUpdateException)
            {
                if (UserExists(user.Id))
                {
                    return Conflict();
                }
                throw;
            }

            return Ok(user);
        }

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
using Webserver.Models;

namespace Webserver.Utils
{
    public static class UserExtensions
    {
        public static void Normalize(this User user)
        {
            user.PhoneNumber = PhoneNumberUtils.NormalizeFormat(user.PhoneNumber);
        }
    }
}
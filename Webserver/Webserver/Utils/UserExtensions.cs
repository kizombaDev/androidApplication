/* This file contains the UserExtensions class.
* This class provides extensions methods to provide easy access to common tasks.
*
* Datei: UserExtensions.cs Autor: Ramandeep Singh
* Datum: 19.01.2016 Version: 1.0
*/

using Webserver.Models;

namespace Webserver.Utils
{
    public static class UserExtensions
    {
        /// <summary>
        ///     Normalizes the phone number of the user.
        /// </summary>
        /// <param name="user"></param>
        public static void Normalize(this User user)
        {
            user.PhoneNumber = PhoneNumberUtils.NormalizeFormat(user.PhoneNumber);
        }
    }
}
/* This file contains the PhoneNumberUtils class.
* This class provides utilities which can be used for the phone number.
*
* Datei: PhoneNumberUtils.cs Autor: Ramandeep Singh
* Datum: 19.01.2016 Version: 1.0
*/

namespace Webserver.Utils
{
    /// <summary>
    ///     Provides phone number utilities.
    /// </summary>
    public static class PhoneNumberUtils
    {
        /// <summary>
        ///     Normalizes the format of a phone number. A leading zero (0) will be replaced by an +49.
        /// </summary>
        /// <param name="phoneNumber">The phone number to normalize.</param>
        /// <returns>The normalized phone number.</returns>
        public static string NormalizeFormat(string phoneNumber)
        {
            if (!string.IsNullOrEmpty(phoneNumber) && phoneNumber.StartsWith("0"))
            {
                phoneNumber = phoneNumber.TrimStart('0').Trim();
                phoneNumber = "+49" + phoneNumber;
            }

            return phoneNumber;
        }
    }
}
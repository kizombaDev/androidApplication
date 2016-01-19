namespace Webserver.Utils
{
    public static class PhoneNumberUtils
    {
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
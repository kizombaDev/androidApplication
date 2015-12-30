using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Dispatcher;

namespace Webserver
{
    public class ExtendedControllerSelector : DefaultHttpControllerSelector
    {
        public ExtendedControllerSelector(HttpConfiguration configuration) : base(configuration)
        {
        }

        public override string GetControllerName(HttpRequestMessage request)
        {
            var controllerName = base.GetControllerName(request);
            return controllerName.Replace("-", string.Empty);
        }
    }
}
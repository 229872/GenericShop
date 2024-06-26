import axios from "axios";
import { useEffect } from "react";
import { environment } from "../utils/constants";
import { useLocation, useNavigate } from "react-router-dom";
import handleAxiosException from "../services/apiService";
import { toast } from "sonner";
import { useTranslation } from "react-i18next";
import { AUTH_PATH, REGISTER_PATH } from "../components/singleuse/Routing";


export default function ConfirmAccountPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { t } = useTranslation()

  // Make it render once
  let flag = true;

  useEffect(() => {
    if (flag) {
      const searchParams = new URLSearchParams(location.search);
      const verificationToken: string | null = searchParams.get('token')
  
  
      if (verificationToken !== null) {
        confirmAccount(verificationToken)
          .then(() => {
            toast.success(t('confirm_account.success'))
            navigate(AUTH_PATH)
          })
          .catch(e => {
            handleAxiosException(e)
            navigate(REGISTER_PATH)
          })
  
      } else {
        navigate(REGISTER_PATH)
        toast.error(t('exception.auth.token.expired'))
      }
      flag = false;
    }

  }, [])

  const confirmAccount = async (token: string) => {
    await axios.put(`${environment.apiBaseUrl}/account/self/register/confirm`, null, { params: {
      token
    }})
  }

  return null;
}
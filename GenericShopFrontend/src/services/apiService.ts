import { t } from "i18next"
import { toast } from "sonner"

export default function handleAxiosException(e: any) {
  if (e.response && e.response.data.message) {
    toast.error(t(e.response.data.message))
  } else {
    toast.error(t('error'))
  }
}

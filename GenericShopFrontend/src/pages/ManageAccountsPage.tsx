import { Card } from "@mui/material"

type ManageAccountsPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
}

export default function ManageAccountsPage({ setLoading, style } : ManageAccountsPageProps ) {
  return (
    <Card elevation={20} sx={{...style}}>
      Manage ManageAccountsPageProps
    </Card>
  )
}
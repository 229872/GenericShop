import { Navigate } from "react-router-dom"

type ProtectedRouteProps = {
  element: React.ReactNode
  shouldRender: boolean
  redirect: string
}

export default function ProtectedElement({ element: Component, shouldRender, redirect } : ProtectedRouteProps ) {
  return (
    shouldRender ? Component : <Navigate replace to={redirect} />
  )
}
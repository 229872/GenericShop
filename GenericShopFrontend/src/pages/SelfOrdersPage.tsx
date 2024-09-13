import { useEffect, useState } from "react"
import { useTranslation } from "react-i18next"
import { BasicOrder, BasicOrderWithFixedPrice, Column } from "../utils/types"
import handleAxiosException from "../services/apiService"
import axios from "axios"
import { environment } from "../utils/constants"
import { getJwtToken } from "../services/tokenService"
import { Button, IconButton, Stack, Tooltip, Typography } from "@mui/material"
import VisibilityIcon from '@mui/icons-material/Visibility';
import RefreshIcon from '@mui/icons-material/Refresh';
import TableWithPagination from "../components/reusable/TableWithPagination"
import ViewOrderDetailsDialog from "../components/singleuse/ViewOrderDetailsDialog"

type OrdersResponse = {
  content: BasicOrder[]
  totalElements: number
}

type OrderActions = {
  actions: JSX.Element
}

type BasicOrderWithActions = BasicOrderWithFixedPrice & OrderActions;

type SelfOrdersPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
}

export default function SelfOrdersPage({ setLoading, style } : SelfOrdersPageProps) {
  const { t } = useTranslation()
  const [ orders, setOrders ] = useState<BasicOrder[]>([])
  const [ sortBy, setSortBy ] = useState<keyof BasicOrderWithActions>('id')
  const [ totalElements, setTotalElements ] = useState<number>(0)
  const [ currentPage, setCurrentPage ] = useState<number>(0)
  const [ pageSize, setPageSize ] = useState<number>(10)
  const [ direction, setDirection ] = useState<'asc' | 'desc'>('asc')
  const [ visibleViewOrderDetailsDialog, setVisibleViewOrderDetailsDialog ] = useState<number | undefined>(undefined)
  const rowsPerPageOptions = [ 5, 10, 15, 20 ]
  const columns: Column<BasicOrderWithActions>[] = [
    { dataProp: 'id', name: t('self_orders.column.id'), label: true },
    { dataProp: 'totalPrice', name: t('self_orders.column.total_price'), label: true },
    { dataProp: 'creationDate', name: t('self_orders.column.creation_date'), label: true },
    { dataProp: 'actions', name: t('self_orders.column.actions'), label: false }
  ]

  useEffect(() => {
    loadOrders(currentPage, pageSize, sortBy, direction)
  }, [])

  const loadOrders = async (pageNr: number, pageSize: number, sortBy: keyof BasicOrderWithActions, direction: 'asc' | 'desc') => {
    try {
      setLoading(true)
      const { data } = await getOrders(pageNr, pageSize, sortBy, direction);
      setOrders(data.content)
      setTotalElements(data.totalElements)

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const getOrders = async (pageNr: number, pageSize: number, sortBy: keyof BasicOrderWithActions, direction: 'asc' | 'desc') => {
    return axios.get<OrdersResponse>(`${environment.apiBaseUrl}/orders?page=${pageNr}&size=${pageSize}&sort=${sortBy},${direction}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const createOrderWithButtons = (order: BasicOrderWithFixedPrice): BasicOrderWithActions => {
    return {
      ...order,
      actions: <Stack direction='row' spacing={2}>
        {
          <Tooltip title={t('self_orders.tooltip.show_more')} placement='top'>
            <IconButton onClick={() => setVisibleViewOrderDetailsDialog(order.id)}>
              <VisibilityIcon />
            </IconButton>
          </Tooltip>
        }
      </Stack>
    };
  };

  const mapOrderPriceToFixed = (order: BasicOrder, positiion: number): BasicOrderWithFixedPrice => {
    return {
      ...order,
      totalPrice: order.totalPrice.toFixed(positiion)
    }
  }
  
  return (
    <Stack sx={{...style}}>
      <Typography textAlign='center' variant='h3'>{t('self_orders.title')}</Typography>
      <Stack direction='row' spacing={5} marginBottom='15px'>
        <Tooltip title={t('self_orders.button.refresh')} placement='top'>
            <Button startIcon={<RefreshIcon />} color='primary' onClick={() => loadOrders(currentPage, pageSize, sortBy, direction)} />
        </Tooltip>
      </Stack>

      <TableWithPagination 
        columns={columns}
        data={orders
          .map((order) => mapOrderPriceToFixed(order, 2))
          .map(createOrderWithButtons)
        } 
        getData={loadOrders}
        totalElements={totalElements}
        sortBy={sortBy}
        setSortBy={setSortBy}
        rowsPerPageOptions={rowsPerPageOptions}
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        pageSize={pageSize}
        setPageSize={setPageSize}
        direction={direction}
        setDirection={setDirection}
        tableStyle={{ width: '100%', maxHeight: '60vh'}}
      />

      {
        visibleViewOrderDetailsDialog &&
        <ViewOrderDetailsDialog
          orderId={visibleViewOrderDetailsDialog}
          open={Boolean(setVisibleViewOrderDetailsDialog)}
          onClose={() => setVisibleViewOrderDetailsDialog(undefined)}
          setLoading={setLoading}
        />
      }

    </Stack>
  )
}
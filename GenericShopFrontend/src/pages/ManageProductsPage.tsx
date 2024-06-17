import { useEffect, useState } from "react"
import { useTranslation } from "react-i18next"
import { BasicProduct, BasicProductWithFixedPrice, Column } from "../utils/types"
import handleAxiosException from "../services/apiService"
import { environment } from "../utils/constants"
import axios from "axios"
import { getJwtToken } from "../services/tokenService"
import { Button, IconButton, Stack, Tooltip, Typography } from "@mui/material"
import VisibilityIcon from '@mui/icons-material/Visibility';
import EditIcon from '@mui/icons-material/Edit';
import RefreshIcon from '@mui/icons-material/Refresh';
import { toast } from "sonner"
import TableWithPagination from "../components/reusable/TableWithPagination"
import CreateProductDialog from "../components/singleuse/CreateProductDialog"
import EditProductDialog from "../components/singleuse/EditProductDialog"
import CreateCategoryDialog from "../components/singleuse/CreateCategoryDialog"
import ViewProductDetailsDialog from "../components/singleuse/ViewProductDetailsDialog"

type ProductsResponse = {
  content: BasicProduct[]
  totalElements: number
}

type ProductActions = {
  actions: JSX.Element
}

type BasicProductWithActions = BasicProductWithFixedPrice & ProductActions;

type ManageProductsPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
}

export default function ManageProductsPage({ setLoading, style } : ManageProductsPageProps) {
  const { t } = useTranslation()
  const [ products, setProducts ] = useState<BasicProduct[]>([])
  const [ sortBy, setSortBy ] = useState<keyof BasicProductWithActions>('id')
  const [ totalElements, setTotalElements ] = useState<number>(0)
  const [ currentPage, setCurrentPage ] = useState<number>(0)
  const [ pageSize, setPageSize ] = useState<number>(10)
  const [ direction, setDirection ] = useState<'asc' | 'desc'>('asc')
  const [ visibleViewProductDetailsDialog, setVisibleViewProductDetailsDialog ] = useState<number | undefined>(undefined)
  const [ visibleEditProductDialog, setVisibleEditProductDialog ] = useState<number | undefined>(undefined)
  const [ visibleCreateProductDialog, setVisibleCreateProductDialog ] = useState<boolean>(false)
  const [ visibleCreateCategoryDialog, setVisibleCreateCategoryDialog ] = useState<boolean>(false)
  const rowsPerPageOptions = [ 5, 10, 15, 20 ]
  const columns: Column<BasicProductWithActions>[] = [
    { dataProp: 'id', name: t('manage_products.column.id'), label: true },
    { dataProp: 'archival', name: t('manage_products.column.archival'), label: true },
    { dataProp: 'name', name: t('manage_products.column.name'), label: true },
    { dataProp: 'price', name: t('manage_products.column.price'), label: true },
    { dataProp: 'quantity', name: t('manage_products.column.quantity'), label: true },
    { dataProp: 'actions', name: t('manage_products.column.actions'), label: false }
  ]

  useEffect(() => {
    loadProducts(currentPage, pageSize, sortBy, direction)
  }, [])

  const loadProducts = async (pageNr: number, pageSize: number, sortBy: keyof BasicProductWithActions, direction: 'asc' | 'desc') => {
    try {
      setLoading(true)
      const { data } = await getProducts(pageNr, pageSize, sortBy, direction);
      setProducts(data.content)
      setTotalElements(data.totalElements)

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const sendArchiveProductRequest = async (productId: number) => {
    try {
      setLoading(true)
      const { data } = await archiveProduct(productId);
      const updatedProducts: BasicProduct[] = products.map(product => 
        product.id === productId ? data : product
      )
      setProducts(updatedProducts)
      toast.success(t('manage_products.operation.success'))

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const archiveProduct = async (productId: number) => {
    return axios.put<BasicProduct>(`${environment.apiBaseUrl}/products/id/${productId}/archive`, null, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const getProducts = async (pageNr: number, pageSize: number, sortBy: keyof BasicProductWithActions, direction: 'asc' | 'desc') => {
    return axios.get<ProductsResponse>(`${environment.apiBaseUrl}/products?page=${pageNr}&size=${pageSize}&sort=${sortBy},${direction}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const createProductWithButtons = (product: BasicProductWithFixedPrice): BasicProductWithActions => {
    return {
      ...product,
      actions: <Stack direction='row' spacing={2}>
        {
          <Tooltip title={t('manage_products.tooltip.show_more')} placement='top'>
            <IconButton onClick={() => setVisibleViewProductDetailsDialog(product.id)}>
              <VisibilityIcon />
            </IconButton>
          </Tooltip>
        }
        {
          !product.archival && (
            <Tooltip title={t('manage_products.tooltip.edit')} placement='top'>
              <IconButton onClick={() => setVisibleEditProductDialog(product.id)}>
                <EditIcon />
              </IconButton>
            </Tooltip>
          )
        }
        {
          !product.archival && 
          <Button variant='contained' color='primary' onClick={() => sendArchiveProductRequest(product.id)}>
            {t('manage_products.button.archive')}
          </Button>
        }
      </Stack>
    };
  };

  const mapProductPriceToFixed = (product: BasicProduct, positiion: number): BasicProductWithFixedPrice => {
    return {
      ...product,
      price: product.price.toFixed(positiion)
    }
  }
  
  return (
    <Stack sx={{...style}}>
      <Typography textAlign='center' variant='h3'>{t('manage_products.title')}</Typography>
      <Stack direction='row' spacing={5} marginBottom='15px'>
        <Tooltip title={t('manage_products.button.refresh')} placement='top'>
            <Button startIcon={<RefreshIcon />} color='primary' onClick={() => loadProducts(currentPage, pageSize, sortBy, direction)} />
        </Tooltip>
        <Button onClick={() => setVisibleCreateProductDialog(true)}>{t('manage_products.button.create_product')}</Button>
        <Button onClick={() => setVisibleCreateCategoryDialog(true)}>{t('manage_products.button.create_category')}</Button>
      </Stack>

      <TableWithPagination 
        columns={columns}
        data={products
          .map((product) => mapProductPriceToFixed(product, 2))
          .map(createProductWithButtons)
        } 
        getData={loadProducts}
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
        visibleCreateProductDialog &&
        <CreateProductDialog
          open={visibleCreateProductDialog}
          onClose={() => setVisibleCreateProductDialog(false)}
          setLoading={setLoading}
        />
      } 


      {
        visibleEditProductDialog &&
        <EditProductDialog
          productId={visibleEditProductDialog}
          open={Boolean(visibleEditProductDialog)}
          onClose={() => setVisibleEditProductDialog(undefined)}
          setLoading={setLoading}
        />
      }

      <CreateCategoryDialog
        open={visibleCreateCategoryDialog}
        onClose={() => setVisibleCreateCategoryDialog(false)}
        setLoading={setLoading}
      />

      {
        visibleViewProductDetailsDialog &&
        <ViewProductDetailsDialog
          productId={visibleViewProductDetailsDialog}
          open={Boolean(setVisibleViewProductDetailsDialog)}
          onClose={() => setVisibleViewProductDetailsDialog(undefined)}
          setLoading={setLoading}
        />
      }

    </Stack>
  )
}
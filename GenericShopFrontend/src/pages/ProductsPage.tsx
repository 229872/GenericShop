import React, { CSSProperties, useEffect, useState } from 'react';
import { Autocomplete, Box, Button, Card, CardContent, CardHeader, Grid, IconButton, ImageList, ImageListItem, Pagination, Stack, TablePagination, TextField, Typography } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import StarIcon from '@mui/icons-material/Star';
import StarHalfIcon from '@mui/icons-material/StarHalf';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import axios from 'axios';
import { environment } from '../utils/constants';
import handleAxiosException from '../services/apiService';
import { BasicProduct } from '../utils/types';
import productNotFound from '/src/assets/no-product-picture.png'
import { useTranslation } from 'react-i18next';
import { Controller, useForm } from 'react-hook-form';
import { getJwtToken } from '../services/tokenService';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';

type DefaultProductData = {
  content: BasicProduct[]
  totalElements: number
}

const schema = z.object({
  category: z.string().min(1)
});

type Category = z.infer<typeof schema>;

type ProductPageProps = {
  setLoading: (state: boolean) => void
  style: CSSProperties
}

const ProductsPage = ({ setLoading, style } : ProductPageProps) => {
  const rowsPerPageOptions = [ 5, 10, 15, 20 ]
  const [ products, setProducts ] = useState<BasicProduct[]>([]);
  const [ categories, setCategories ] = useState<string[]>([]);
  const [ pickedCategory, setPickedCategory ] = useState<string | undefined>(undefined)
  const [ totalElements, setTotalElements ] = useState<number>(0)
  const [ pageSize, setPageSize ] = useState<number>(10);
  const [ currentPage, setCurrentPage ] = useState<number>(1);
  const { t } = useTranslation();

  const { control, watch, formState, handleSubmit } = useForm<Category>({
    resolver: zodResolver(schema)
  });
  const { errors, isValid } = formState;

  useEffect(() => {
    fetchProducts(currentPage, pageSize);
    fetchCategories()
  }, []);

  const fetchProducts = async (pageNr: number, pageSize: number) => {
    try {
      setLoading(true);
      const { data } = await getProducts(pageNr - 1, pageSize);
      const products: BasicProduct[] = data.content;
      setProducts(products)
      setTotalElements(data.totalElements)

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  };
  
  const fetchCategories = async () => {
    try {
      setLoading(true);
      const { data } = await getCategories();
      setCategories(data);

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const getProducts = async (pageNr: number, pageSize: number) => {
    return axios.get<DefaultProductData>(`${environment.apiBaseUrl}/products?page=${pageNr}&size=${pageSize}`);
  }

  const getCategories = async () => {
    return axios.get<string[]>(`${environment.apiBaseUrl}/categories`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    });
  }

  const onPageChange = (event: any, pageNumber: number) => {
    setCurrentPage(pageNumber);
    fetchProducts(pageNumber, pageSize)
  };

  const onResetClicked = () => {
    fetchProducts(currentPage, pageSize);
  };

  const changeRowsPerPage = (event: any): void => {
    const newPageSize = event.target.value
    setPageSize(newPageSize)
    fetchProducts(currentPage, newPageSize)
  }

  const onValid = (data: Category) => {
    setPickedCategory(data.category)
  }

  const getStarArray = (averageRating: number) => {
    const fullStars = Math.floor(averageRating);
    const halfStars = Math.ceil(averageRating - fullStars);
    const emptyStars = 5 - fullStars - halfStars;
    return [
      ...Array(fullStars).fill(<StarIcon />),
      ...Array(halfStars).fill(<StarHalfIcon />),
      ...Array(emptyStars).fill(<StarBorderIcon />)
    ];
  };

  return (
    <Box sx={{ ...style }}>
      <Stack spacing={2}>
        <Stack direction='row' spacing={4} justifyContent='center'>
          <IconButton onClick={onResetClicked}>
            <RefreshIcon />
          </IconButton>

          <Box sx={{ width: '40%' }}>
            <form onSubmit={handleSubmit(onValid)} noValidate>
              <Stack direction='row' spacing={3}>
                <Controller
                  name='category'
                  control={control}
                  render={({ field }) => (
                    <Autocomplete
                      options={categories}
                      onChange={(e, value) => field.onChange(value)}
                      isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
                      value={field.value || ''}
                      fullWidth
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          label={t('manage_products.view_product.label.category')}
                          error={Boolean(errors.category)}
                          helperText={errors.category?.message && t(errors.category.message)}
                          placeholder={t('manage_products.view_product.enter.category')}
                        />
                      )}
                    />
                  )}
                />

                <Button type='submit' variant='contained' disabled={!isValid || pickedCategory === watch('category') }>{t('manage_products.view_product.find')}</Button>
              </Stack>
            </form>
          </Box>
        </Stack>

        <Box display='flex' justifyContent='space-between' alignItems='center'>
          <Box>
            <Typography variant='h6'>{ pickedCategory ? t(pickedCategory) : t('manage_products.view_product.all') }:</Typography>
          </Box>
          <Box flexGrow={1} display='flex' justifyContent='center'>
            <Pagination
              count={Math.ceil(totalElements / pageSize)}
              page={currentPage}
              onChange={onPageChange}
            />
          </Box>
        </Box>
      </Stack>



      <ImageList cols={5} gap={15} sx={{ marginTop: '20px' }}>
        {products.map((product, index) => (
          <ImageListItem key={index}>
            <Card>
              <CardHeader
                title={
                  <Box
                    marginTop='30px'
                    marginRight='10px'
                    component="img"
                    sx={{
                      height: 120,
                      width: '50%',
                      maxHeight: { xs: 150, md: 150 },
                      maxWidth: { xs: '100%', md: '100%' },
                    }}
                    alt={t('manage_products.view_product.label.product_image')}
                    src={product?.imageUrl ?? productNotFound}
                  />
                }
              />
              <CardContent className="content-left">
                <Box className="card-title">{product.name}</Box>
                <Box><strong>Price: {product.price},-</strong></Box>
                <Box className="available-message">
                  {product.archival || product.quantity === 0 ? (
                    <>Unavailable</>
                  ) : (
                    <>Available</>
                  )}
                </Box>
                {/* <Box className="star-rating">
                  {getStarArray(product.averageRating)}
                </Box> */}
                {/* <Box className="d-inline-block g-color-primary rating">
                  Rating {product.averageRating}
                </Box> */}
              </CardContent>
            </Card>
          </ImageListItem>
        ))}
      </ImageList>

      <TablePagination
        component='div'
        page={currentPage}
        onPageChange={onPageChange}
        count={totalElements}
        rowsPerPage={pageSize}
        rowsPerPageOptions={rowsPerPageOptions}
        onRowsPerPageChange={changeRowsPerPage}
      />
    </Box>
  );
};

export default ProductsPage;
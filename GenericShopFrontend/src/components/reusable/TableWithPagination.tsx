import { Card, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TableSortLabel, Typography } from "@mui/material"
import { FormEvent, ReactNode, useState } from "react"
import { useTranslation } from "react-i18next"
import { Column, HasId } from "../../utils/types"
import { AxiosResponse } from "axios"

type TableWithPaginationProps<T extends HasId> = {
  columns: Column<T>[]
  data: T[]
  totalElements: number
  getData: (pageNr: number, pageSize: number, sortBy: keyof T, direction: 'asc' | 'desc') => Promise<AxiosResponse<T[]>>
  rowsPerPageOptions: number[]
  tableStyle?: React.CSSProperties
  headerStyle?: React.CSSProperties
  contentStyle?: React.CSSProperties
}

export default function TableWithPagination<T extends HasId>({ columns, data, totalElements, getData,
   rowsPerPageOptions, tableStyle, headerStyle, contentStyle } : TableWithPaginationProps<T>) {
  const { t } = useTranslation();
  const [currentPage, setCurrentPage] = useState<number>(0)
  const [pageSize, setPageSize] = useState<number>(10)
  const [sortBy, setSortBy] = useState<keyof T>('id')
  const [direction, setDirection] = useState<'asc' | 'desc'>('asc')

  const changePage = (event: any, page: number): void => {
    setCurrentPage(page)
    getData(page, pageSize, sortBy, direction)
  }

  const changeRowsPerPage = (event: any): void => {
    const newPageSize = event.target.value
    setPageSize(newPageSize)
    getData(currentPage, newPageSize, sortBy, direction)
  }

  const handleSortChange = (event: FormEvent<HTMLSpanElement>, column: keyof T | 'id'): void => {
    
    if (column === sortBy) {
      const newDirection = direction === 'asc' ? 'desc' : 'asc'
      changeDirection(newDirection)
    } else {
      changeSortBy(column)
    }
  }

  const changeDirection = (newDirection: 'asc' | 'desc') => {
    setDirection(newDirection)
    getData(currentPage, pageSize, sortBy, newDirection)
  }

  const changeSortBy = (newSortBy: keyof T | 'id') => {
    setSortBy(newSortBy)
    getData(currentPage, pageSize, newSortBy, direction)
  }

  const getPropertyAsNode = <T, K extends keyof T>(obj: T, key: K): ReactNode => {
    const value = obj[key];
    if (typeof value === 'number' || typeof value === 'string') {
      return value;
    } else if ( typeof value === 'boolean') {
      return value ? t('table.yes') : t('table.no')
    } else {
      return 'Not simple value'
    }
  }

  if (data.length == 0) {
    return <Typography variant='h6'>{t('table.no_content')}</Typography>
  } else {
    return (
      <Card elevation={10} sx={{...tableStyle}}>
        <TableContainer>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                {
                  columns.map((column, key) => (
                    <TableCell key={key} sx={{ ...headerStyle }}>
                      <TableSortLabel
                        active={sortBy === column.dataProp}
                        direction={direction}
                        onClick={e => handleSortChange(e, column.dataProp)}
                      >
                        {column.name}
                      </TableSortLabel>
                    </TableCell>
                  ))
                }
              </TableRow>
            </TableHead>
            <TableBody>
              {
                data && data.map((obj, key) => (
                  <TableRow key={key}>
                    {
                      columns.map((col, key) => (
                        <TableCell key={key} sx={{...contentStyle}}>
                          {
                            getPropertyAsNode(obj, col.dataProp)
                          }
                        </TableCell>
                      ))
                    }
                  </TableRow>
                ))
              }
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          component='div'
          page={currentPage}
          onPageChange={changePage}
          count={totalElements}
          rowsPerPage={pageSize}
          rowsPerPageOptions={rowsPerPageOptions}
          onRowsPerPageChange={changeRowsPerPage}
        />
      </Card>
    )
  }
}
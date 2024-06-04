import { Card, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TableSortLabel, Typography } from "@mui/material"
import { FormEvent, useState } from "react"
import { useTranslation } from "react-i18next"

type TableWithPaginationProps = {
  columns: Column[]
  data: any[]
  getData: (pageNr: number, pageSize: number, sortBy: string, direction: 'asc' | 'desc') => any[]
  rowsPerPageOptions: number[]
  tableStyle?: React.CSSProperties
  headerStyle?: React.CSSProperties
  contentStyle?: React.CSSProperties
}

type Column = {
  dataProp: string
  name: string
}

export default function TableWithPagination({ columns, data, getData, rowsPerPageOptions, tableStyle, headerStyle, contentStyle } : TableWithPaginationProps) {
  const { t } = useTranslation();
  const [currentPage, setCurrentPage] = useState<number>(0)
  const [pageSize, setPageSize] = useState<number>(10)
  const [totalElements, setTotalElements] = useState<number>(0)
  const [sortBy, setSortBy] = useState<string>('id')
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

  const handleSortChange = (event: FormEvent<HTMLSpanElement>, column: string): void => {
    
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

  const changeSortBy = (newSortBy: string) => {
    setSortBy(newSortBy)
    getData(currentPage, pageSize, newSortBy, direction)
  }

  if (data.length == 0) {
    return <Typography variant='h3'>{t('table.no_content')}</Typography>
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
                      columns
                        .map((col) => col.dataProp)
                        .map((prop, key) => (
                          <TableCell key={key} sx={{ ...contentStyle }}>
                            {obj[prop]}
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
export default class PersonDto {
  id?: number
  name?: string
  istId?: string
  email?: string
  type?: string
  workflowTese?: string
  workflowDef?: string
  description?: string
  teachers?: number[]
  grade?: number

  constructor(obj?: Partial<PersonDto>) {
    Object.assign(this, obj)
  }
}

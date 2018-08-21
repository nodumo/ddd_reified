/*
 * Copyright 2011-2013 Sisioh Project and others. (http://www.sisioh.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.sisioh.dddbase.lifecycle.memory.async

import org.sisioh.dddbase.core.lifecycle.async._
import org.sisioh.dddbase.core.lifecycle.{ EntityIOContext, EntitiesChunk }
import org.sisioh.dddbase.core.model._
import scala.concurrent._

/**
 * `AsyncRepositoryOnMemorySupport`に`AsyncEntityReadableAsPredicate`のための機能を追加するトレイト。
 *
 * @tparam ID 識別子の型
 * @tparam E エンティティの型
 */
trait AsyncRepositoryOnMemorySupportAsPredicate[ID <: Identifier[_], E <: Entity[ID] with EntityCloneable[ID, E]]
    extends AsyncEntityReadableAsPredicate[ID, E] {
  this: AsyncRepositoryOnMemory[ID, E] =>

  def filterBy(predicate: (E) => Boolean, indexOpt: Option[Int], maxEntitiesOpt: Option[Int])(implicit ctx: EntityIOContext[Future]): Future[EntitiesChunk[ID, E]] = {
    implicit val executor = getExecutionContext(ctx)
    Future {
      val filteredSubEntities = getEntities.values.toList.filter(predicate)
      val index = indexOpt.getOrElse(0)
      val maxEntities = maxEntitiesOpt.getOrElse(filteredSubEntities.size)
      val subEntities = filteredSubEntities.slice(index * maxEntities, index * maxEntities + maxEntities)
      EntitiesChunk(index, subEntities)
    }
  }

}

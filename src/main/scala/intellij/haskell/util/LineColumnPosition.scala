/*
 * Copyright 2014-2018 Rik van der Kleij
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intellij.haskell.util

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

case class LineColumnPosition(lineNr: Int, columnNr: Int) extends Ordered[LineColumnPosition] {

  def compare(that: LineColumnPosition): Int = {
    val lineNrCompare = this.lineNr compare that.lineNr
    if (lineNrCompare == 0) {
      this.columnNr compare that.columnNr
    } else {
      lineNrCompare
    }
  }
}

object LineColumnPosition {

  def fromOffset(psiFile: PsiFile, offset: Int, runInRead: Boolean = false): Option[LineColumnPosition] = {
    for {
      doc <- HaskellFileUtil.findDocument(psiFile, runInRead)
      li <- if (offset <= doc.getTextLength) Some(doc.getLineNumber(offset)) else None
    } yield LineColumnPosition(li + 1, offset - doc.getLineStartOffset(li) + 1)
  }

  def getOffset(psiFile: PsiFile, lineColPos: LineColumnPosition, runInRead: Boolean = false): Option[Int] = {
    for {
      doc <- HaskellFileUtil.findDocument(psiFile, runInRead)
      lineIndex <- getLineIndex(lineColPos.lineNr, doc)
      startOffsetLine = doc.getLineStartOffset(lineIndex)
    } yield startOffsetLine + lineColPos.columnNr - 1
  }

  private def getLineIndex(lineNr: Int, doc: Document) = {
    if (lineNr > doc.getLineCount) {
      None
    } else {
      Some(lineNr - 1)
    }
  }
}
